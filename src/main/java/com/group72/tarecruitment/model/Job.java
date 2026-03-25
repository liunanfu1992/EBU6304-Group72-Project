package com.group72.tarecruitment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.group72.tarecruitment.util.SkillCatalog;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    private String id;
    private String title;
    private String moduleCode;
    private String description;
    private List<String> requiredSkills = new ArrayList<>();
    private Integer weeklyHours;
    private String moUserId;
    private String status;

    public Job() {
    }

    public Job(String id, String title, String moduleCode, String description, List<String> requiredSkills,
               Integer weeklyHours, String moUserId, String status) {
        this.id = id;
        this.title = title;
        this.moduleCode = moduleCode;
        this.description = description;
        if (requiredSkills != null) {
            this.requiredSkills = requiredSkills;
        }
        this.weeklyHours = weeklyHours;
        this.moUserId = moUserId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills == null ? new ArrayList<>() : new ArrayList<>(requiredSkills);
    }

    public Integer getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(Integer weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public String getMoUserId() {
        return moUserId;
    }

    public void setMoUserId(String moUserId) {
        this.moUserId = moUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    public List<String> getPredefinedRequiredSkills() {
        return SkillCatalog.extractPredefinedSkills(requiredSkills);
    }

    @JsonIgnore
    public List<String> getCustomRequiredSkills() {
        return SkillCatalog.extractCustomSkills(requiredSkills);
    }

    @JsonIgnore
    public String getCustomRequiredSkillsInput() {
        return SkillCatalog.joinSkills(getCustomRequiredSkills());
    }
}
