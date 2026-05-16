package com.group72.tarecruitment.model;

import java.util.List;

public class AiMatchAnalysisResult {
    private final boolean success;
    private final String summary;
    private final List<String> strengths;
    private final List<String> gaps;
    private final List<String> interviewFocus;
    private final String recommendation;
    private final String disclaimer;
    private final String errorMessage;

    public AiMatchAnalysisResult(
            boolean success,
            String summary,
            List<String> strengths,
            List<String> gaps,
            List<String> interviewFocus,
            String recommendation,
            String disclaimer,
            String errorMessage
    ) {
        this.success = success;
        this.summary = safeString(summary);
        this.strengths = safeList(strengths);
        this.gaps = safeList(gaps);
        this.interviewFocus = safeList(interviewFocus);
        this.recommendation = safeString(recommendation);
        this.disclaimer = safeString(disclaimer);
        this.errorMessage = safeString(errorMessage);
    }

    public static AiMatchAnalysisResult success(
            String summary,
            List<String> strengths,
            List<String> gaps,
            List<String> interviewFocus,
            String recommendation,
            String disclaimer
    ) {
        return new AiMatchAnalysisResult(true, summary, strengths, gaps, interviewFocus, recommendation, disclaimer, "");
    }

    public static AiMatchAnalysisResult failure(String errorMessage) {
        return new AiMatchAnalysisResult(false, "", List.of(), List.of(), List.of(), "", "", errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public List<String> getGaps() {
        return gaps;
    }

    public List<String> getInterviewFocus() {
        return interviewFocus;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private static List<String> safeList(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }

    private static String safeString(String value) {
        return value == null ? "" : value.trim();
    }
}
