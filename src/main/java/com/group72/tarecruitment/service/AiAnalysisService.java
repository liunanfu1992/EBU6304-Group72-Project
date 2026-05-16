package com.group72.tarecruitment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.AiMatchAnalysisResult;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.MoApplicationView;
import com.group72.tarecruitment.model.Profile;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AiAnalysisService {
    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final String DEFAULT_MODEL = "gpt-4o-mini";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;
    private final String model;

    public AiAnalysisService() {
        this(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(),
                new ObjectMapper(),
                configuredValue("AI_API_BASE_URL", DEFAULT_BASE_URL),
                configuredValue("AI_API_KEY", ""),
                configuredValue("AI_MODEL", DEFAULT_MODEL)
        );
    }

    public AiAnalysisService(HttpClient httpClient, ObjectMapper objectMapper, String baseUrl, String apiKey, String model) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = trimTrailingSlash(baseUrl == null || baseUrl.isBlank() ? DEFAULT_BASE_URL : baseUrl.trim());
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model == null || model.isBlank() ? DEFAULT_MODEL : model.trim();
    }

    public AiMatchAnalysisResult generateMoAnalysis(MoApplicationView applicationView) {
        if (applicationView == null) {
            return AiMatchAnalysisResult.failure("Application context is unavailable.");
        }
        if (apiKey.isBlank()) {
            return AiMatchAnalysisResult.failure("AI analysis is not configured on this server.");
        }

        try {
            String responseBody = sendChatCompletion(buildMessages(applicationView));
            return parseChatCompletion(responseBody);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return AiMatchAnalysisResult.failure("AI analysis was interrupted. Please try again.");
        } catch (Exception exception) {
            return AiMatchAnalysisResult.failure("AI analysis could not be generated. Please try again later.");
        }
    }

    public String buildUserPrompt(MoApplicationView applicationView) {
        Job job = applicationView.getJob();
        Profile profile = applicationView.getProfile();
        return """
                Analyze this TA application for the module organizer.

                Job:
                - Title: %s
                - Module: %s
                - Weekly hours: %s
                - Description: %s
                - Required skills: %s

                Candidate:
                - Name: %s
                - Major: %s
                - Profile skills: %s
                - CV uploaded: %s

                Existing rule-based match:
                - Score: %d%%
                - Label: %s
                - Evidence: %s
                - Matched skills: %s
                - Missing skills: %s
                - Application status: %s

                Return JSON only in this exact shape:
                {
                  "summary": "brief match summary",
                  "strengths": ["evidence-based strength"],
                  "gaps": ["missing skill or risk"],
                  "interviewFocus": ["question or area to verify"],
                  "recommendation": "non-binding review recommendation",
                  "disclaimer": "decision support only"
                }
                """.formatted(
                job == null ? "Unavailable" : safe(job.getTitle()),
                job == null ? "Unavailable" : safe(job.getModuleCode()),
                job == null || job.getWeeklyHours() == null ? "Unavailable" : job.getWeeklyHours() + " hours",
                job == null ? "Unavailable" : safe(job.getDescription()),
                job == null ? "[]" : job.getRequiredSkills(),
                applicationView.getCandidateDisplayName(),
                applicationView.getMajorDisplay(),
                profile == null ? "[]" : profile.getAllSkills(),
                applicationView.hasCv() ? "yes" : "no",
                applicationView.getMatchPercent(),
                applicationView.getMatchLabel(),
                applicationView.getMatchEvidenceSummary(),
                applicationView.getMatchedSkills(),
                applicationView.getMissingSkills(),
                applicationView.getStatusLabel()
        );
    }

    private List<Map<String, String>> buildMessages(MoApplicationView applicationView) {
        return List.of(
                Map.of(
                        "role", "system",
                        "content", """
                                You are an assistant helping a module organizer review a teaching assistant application.
                                Use only the provided information. Do not invent experience, qualifications, or CV details.
                                Do not make judgments about protected or irrelevant personal traits.
                                The recommendation must be non-binding decision support only.
                                If information is missing, say it is unavailable.
                                Return concise JSON only.
                                """
                ),
                Map.of("role", "user", "content", buildUserPrompt(applicationView))
        );
    }

    private String sendChatCompletion(List<Map<String, String>> messages) throws IOException, InterruptedException {
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", messages,
                "temperature", 0.2,
                "max_tokens", 900,
                "response_format", Map.of("type", "json_object")
        );
        String requestBody = objectMapper.writeValueAsString(payload);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .timeout(REQUEST_TIMEOUT)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("AI API returned status " + response.statusCode());
        }
        return response.body();
    }

    public AiMatchAnalysisResult parseChatCompletion(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        String content = root.path("choices").path(0).path("message").path("content").asText("");
        if (content.isBlank()) {
            return AiMatchAnalysisResult.failure("AI response did not include analysis content.");
        }

        JsonNode analysis = objectMapper.readTree(content);
        return AiMatchAnalysisResult.success(
                analysis.path("summary").asText(""),
                toStringList(analysis.path("strengths")),
                toStringList(analysis.path("gaps")),
                toStringList(analysis.path("interviewFocus")),
                analysis.path("recommendation").asText(""),
                analysis.path("disclaimer").asText("")
        );
    }

    private List<String> toStringList(JsonNode node) {
        List<String> values = new ArrayList<>();
        if (node != null && node.isArray()) {
            node.forEach(item -> {
                String value = item.asText("");
                if (!value.isBlank()) {
                    values.add(value.trim());
                }
            });
        }
        return values;
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "Unavailable" : value.trim();
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String configValue(Properties config, String key, String defaultValue) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String fileValue = config.getProperty(key);
        return fileValue == null || fileValue.isBlank() ? defaultValue : fileValue;
    }

    private static String configuredValue(String key, String defaultValue) {
        return configValue(loadConfigProperties(), key, defaultValue);
    }

    private static Properties loadConfigProperties() {
        Properties properties = new Properties();
        for (Path configPath : resolveConfigPaths()) {
            if (configPath == null || Files.notExists(configPath)) {
                continue;
            }
            try (var inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
                return properties;
            } catch (IOException exception) {
                return new Properties();
            }
        }
        try (InputStream inputStream = AiAnalysisService.class.getClassLoader().getResourceAsStream("ai.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            return new Properties();
        }
        return properties;
    }

    private static List<Path> resolveConfigPaths() {
        String explicitPath = envOrDefault("AI_CONFIG_FILE", "");
        if (explicitPath.isBlank()) {
            explicitPath = System.getProperty("AI_CONFIG_FILE", "");
        }
        if (explicitPath.isBlank()) {
            explicitPath = System.getProperty("ai.config.file", "");
        }
        if (!explicitPath.isBlank()) {
            return List.of(Path.of(explicitPath));
        }
        List<Path> paths = new ArrayList<>();
        try {
            paths.add(AppConfig.resolveConfigFile("ai.properties"));
        } catch (IllegalStateException exception) {
            // Fall back to process-local paths below.
        }
        paths.add(Path.of(System.getProperty("user.home"), ".ta-recruitment-system", "config", "ai.properties"));
        paths.add(Path.of(System.getProperty("user.dir"), ".ta-recruitment-system", "config", "ai.properties"));
        return paths;
    }

    private static String trimTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
