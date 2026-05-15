package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group72.tarecruitment.model.AiMatchAnalysisResult;
import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.MoApplicationView;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.service.AiAnalysisService;
import java.net.http.HttpClient;
import java.util.List;
import org.junit.jupiter.api.Test;

class AiAnalysisServiceTest {
    @Test
    void generateMoAnalysisShouldFailWhenApiKeyIsMissing() {
        AiAnalysisService service = new AiAnalysisService(HttpClient.newHttpClient(), new ObjectMapper(), "https://api.example.com", "", "model");

        AiMatchAnalysisResult result = service.generateMoAnalysis(buildApplicationView());

        assertFalse(result.isSuccess());
        assertEquals("AI analysis is not configured on this server.", result.getErrorMessage());
    }

    @Test
    void buildUserPromptShouldIncludeStructuredApplicationContext() {
        AiAnalysisService service = new AiAnalysisService(HttpClient.newHttpClient(), new ObjectMapper(), "https://api.example.com", "key", "model");

        String prompt = service.buildUserPrompt(buildApplicationView());

        assertTrue(prompt.contains("Programming TA"));
        assertTrue(prompt.contains("CS101"));
        assertTrue(prompt.contains("Alice"));
        assertTrue(prompt.contains("Java"));
        assertTrue(prompt.contains("Communication"));
        assertTrue(prompt.contains("50%"));
        assertTrue(prompt.contains("Matched skills: [Java]"));
        assertTrue(prompt.contains("Missing skills: [Communication]"));
        assertTrue(prompt.contains("CV uploaded: yes"));
    }

    @Test
    void parseChatCompletionShouldMapJsonContentToResultFields() throws Exception {
        AiAnalysisService service = new AiAnalysisService(HttpClient.newHttpClient(), new ObjectMapper(), "https://api.example.com", "key", "model");
        String responseBody = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "{\\"summary\\":\\"Good structured fit.\\",\\"strengths\\":[\\"Matches Java\\"],\\"gaps\\":[\\"Missing Communication\\"],\\"interviewFocus\\":[\\"Ask about tutorials\\"],\\"recommendation\\":\\"Consider shortlisting with interview verification.\\",\\"disclaimer\\":\\"Decision support only.\\"}"
                      }
                    }
                  ]
                }
                """;

        AiMatchAnalysisResult result = service.parseChatCompletion(responseBody);

        assertTrue(result.isSuccess());
        assertEquals("Good structured fit.", result.getSummary());
        assertEquals(List.of("Matches Java"), result.getStrengths());
        assertEquals(List.of("Missing Communication"), result.getGaps());
        assertEquals(List.of("Ask about tutorials"), result.getInterviewFocus());
        assertEquals("Consider shortlisting with interview verification.", result.getRecommendation());
        assertEquals("Decision support only.", result.getDisclaimer());
    }

    private MoApplicationView buildApplicationView() {
        Application application = new Application("app-1", "ta-1", "job-1", Application.STATUS_PENDING, 1L, 2L);
        Job job = new Job(
                "job-1",
                "Programming TA",
                "CS101",
                "Support Java labs.",
                List.of("Java", "Communication"),
                6,
                "mo-1",
                Job.STATUS_OPEN
        );
        User taUser = new User("ta-1", "alice", "", Role.TA, "alice@example.com");
        Profile profile = new Profile(
                "ta-1",
                "Alice",
                "20260001",
                "Computer Science",
                "alice@example.com",
                List.of("Java"),
                List.of(),
                "alice-cv.pdf"
        );
        return new MoApplicationView(application, job, taUser, profile);
    }
}
