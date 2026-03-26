package com.group72.tarecruitment.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.group72.tarecruitment.util.SkillCatalog;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
    private String userId;
    private String name;
    private String studentId;
    private String major;
    private String email;
    @JsonAlias("skills")
    private List<String> selectedSkills = new ArrayList<>();
    private List<String> customSkills = new ArrayList<>();
    private String cvPath;

    public Profile() {
    }

    public Profile(String userId, String name, String studentId, String major, String email,
                   List<String> selectedSkills, List<String> customSkills, String cvPath) {
        this.userId = userId;
        this.name = name;
        this.studentId = studentId;
        this.major = major;
        this.email = email;
        if (selectedSkills != null) {
            this.selectedSkills = selectedSkills;
        }
        if (customSkills != null) {
            this.customSkills = customSkills;
        }
        this.cvPath = cvPath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSelectedSkills() {
        return selectedSkills;
    }

    public void setSelectedSkills(List<String> selectedSkills) {
        this.selectedSkills = selectedSkills == null ? new ArrayList<>() : new ArrayList<>(selectedSkills);
    }

    public List<String> getCustomSkills() {
        return customSkills;
    }

    public void setCustomSkills(List<String> customSkills) {
        this.customSkills = customSkills == null ? new ArrayList<>() : new ArrayList<>(customSkills);
    }

    @JsonIgnore
    public List<String> getAllSkills() {
        return SkillCatalog.mergeSkills(selectedSkills, customSkills);
    }

    @JsonIgnore
    public String getCustomSkillsInput() {
        return SkillCatalog.joinSkills(customSkills);
    }

    @JsonIgnore
    public boolean hasSelectedSkills() {
        return selectedSkills != null && !selectedSkills.isEmpty();
    }

    public String getCvPath() {
        return cvPath;
    }

    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }

    @JsonIgnore
    public boolean hasCv() {
        return cvPath != null && !cvPath.isBlank();
    }

    @JsonIgnore
    public String getCvFileName() {
        return hasCv() ? cvPath : "";
    }
}
