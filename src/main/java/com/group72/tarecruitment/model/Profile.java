package com.group72.tarecruitment.model;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private String userId;
    private String name;
    private String studentId;
    private String major;
    private List<String> skills = new ArrayList<>();
    private String cvPath;

    public Profile() {
    }

    public Profile(String userId, String name, String studentId, String major, List<String> skills, String cvPath) {
        this.userId = userId;
        this.name = name;
        this.studentId = studentId;
        this.major = major;
        if (skills != null) {
            this.skills = skills;
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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getCvPath() {
        return cvPath;
    }

    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }
}
