package com.group72.tarecruitment.model;

import java.util.List;

public class JobMatchView {
    private final Job job;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final int matchPercent;

    public JobMatchView(Job job, List<String> matchedSkills, List<String> missingSkills, int matchPercent) {
        this.job = job;
        this.matchedSkills = matchedSkills == null ? List.of() : List.copyOf(matchedSkills);
        this.missingSkills = missingSkills == null ? List.of() : List.copyOf(missingSkills);
        this.matchPercent = matchPercent;
    }

    public Job getJob() {
        return job;
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
            return "Strong match";
        }
        if (matchPercent >= 50) {
            return "Partial match";
        }
        if (matchPercent > 0) {
            return "Limited match";
        }
        return "No match yet";
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
}
