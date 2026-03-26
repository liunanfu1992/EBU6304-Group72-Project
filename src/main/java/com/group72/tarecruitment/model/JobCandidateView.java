package com.group72.tarecruitment.model;

import java.util.List;

public class JobCandidateView {
    private final Profile profile;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final int matchPercent;

    public JobCandidateView(Profile profile, List<String> matchedSkills, List<String> missingSkills, int matchPercent) {
        this.profile = profile;
        this.matchedSkills = matchedSkills == null ? List.of() : List.copyOf(matchedSkills);
        this.missingSkills = missingSkills == null ? List.of() : List.copyOf(missingSkills);
        this.matchPercent = matchPercent;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public int getMatchPercent() {
        return matchPercent;
    }

    public String getMatchLabel() {
        if (missingSkills.isEmpty()) {
            return "Strong candidate";
        }
        if (matchPercent >= 50) {
            return "Promising candidate";
        }
        if (matchPercent > 0) {
            return "Partial candidate";
        }
        return "Low match";
    }

    public String getMatchTone() {
        if (missingSkills.isEmpty()) {
            return "success";
        }
        if (matchPercent >= 50) {
            return "info";
        }
        if (matchPercent > 0) {
            return "warning";
        }
        return "muted";
    }

    public String getDisplayName() {
        if (profile == null || profile.getName() == null || profile.getName().isBlank()) {
            return "Unnamed TA";
        }
        return profile.getName();
    }
}
