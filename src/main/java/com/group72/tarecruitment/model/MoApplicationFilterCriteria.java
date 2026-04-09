package com.group72.tarecruitment.model;

import com.group72.tarecruitment.util.SkillCatalog;
import java.util.List;

public class MoApplicationFilterCriteria {
    private final String keyword;
    private final String major;
    private final String status;
    private final List<String> selectedSkills;

    public MoApplicationFilterCriteria(String keyword, String major, String status, List<String> selectedSkills) {
        this.keyword = normalize(keyword);
        this.major = normalize(major);
        this.status = normalize(status);
        this.selectedSkills = SkillCatalog.normalizeSelectedSkills(selectedSkills);
    }

    public String getKeyword() {
        return keyword;
    }

    public String getMajor() {
        return major;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getSelectedSkills() {
        return selectedSkills;
    }

    public boolean hasActiveFilters() {
        return !keyword.isEmpty() || !major.isEmpty() || !status.isEmpty() || !selectedSkills.isEmpty();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
