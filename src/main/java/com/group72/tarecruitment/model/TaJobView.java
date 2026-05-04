package com.group72.tarecruitment.model;

import java.util.List;

public class TaJobView {
    private final JobMatchView matchView;
    private final String moduleOwnerDisplayName;
    private final String moduleOwnerEmail;

    public TaJobView(JobMatchView matchView, String moduleOwnerDisplayName, String moduleOwnerEmail) {
        this.matchView = matchView;
        this.moduleOwnerDisplayName = moduleOwnerDisplayName;
        this.moduleOwnerEmail = moduleOwnerEmail;
    }

    public JobMatchView getMatchView() {
        return matchView;
    }

    public Job getJob() {
        return matchView.getJob();
    }

    public List<String> getMatchedSkills() {
        return matchView.getMatchedSkills();
    }

    public boolean isMatchedSkill(String skill) {
        return containsSkillIgnoreCase(getMatchedSkills(), skill);
    }

    public List<String> getMissingSkills() {
        return matchView.getMissingSkills();
    }

    public boolean isMissingSkill(String skill) {
        return containsSkillIgnoreCase(getMissingSkills(), skill);
    }

    public int getMatchPercent() {
        return matchView.getMatchPercent();
    }

    public String getMatchLabel() {
        return matchView.getMatchLabel();
    }

    public String getMatchTone() {
        return matchView.getMatchTone();
    }

    public String getModuleOwnerDisplayName() {
        return moduleOwnerDisplayName;
    }

    public String getModuleOwnerEmail() {
        return moduleOwnerEmail;
    }

    private boolean containsSkillIgnoreCase(List<String> skills, String candidate) {
        if (skills == null || candidate == null || candidate.isBlank()) {
            return false;
        }
        for (String skill : skills) {
            if (skill != null && skill.trim().equalsIgnoreCase(candidate.trim())) {
                return true;
            }
        }
        return false;
    }
}
