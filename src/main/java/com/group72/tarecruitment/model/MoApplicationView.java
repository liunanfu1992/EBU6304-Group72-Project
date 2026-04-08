package com.group72.tarecruitment.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MoApplicationView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private final Application application;
    private final Job job;
    private final User taUser;
    private final Profile profile;

    public MoApplicationView(Application application, Job job, User taUser, Profile profile) {
        this.application = application;
        this.job = job;
        this.taUser = taUser;
        this.profile = profile;
    }

    public Application getApplication() {
        return application;
    }

    public Job getJob() {
        return job;
    }

    public User getTaUser() {
        return taUser;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getCandidateDisplayName() {
        if (profile != null && profile.getName() != null && !profile.getName().isBlank()) {
            return profile.getName();
        }
        if (taUser != null && taUser.getUsername() != null && !taUser.getUsername().isBlank()) {
            return taUser.getUsername();
        }
        return "Unknown candidate";
    }

    public String getCandidateEmail() {
        if (profile != null && profile.getEmail() != null && !profile.getEmail().isBlank()) {
            return profile.getEmail();
        }
        return taUser == null ? "" : taUser.getEmail();
    }

    public String getStudentIdDisplay() {
        return profile == null || profile.getStudentId() == null || profile.getStudentId().isBlank()
                ? "-"
                : profile.getStudentId();
    }

    public String getMajorDisplay() {
        return profile == null || profile.getMajor() == null || profile.getMajor().isBlank()
                ? "-"
                : profile.getMajor();
    }

    public boolean hasCv() {
        return profile != null && profile.hasCv();
    }

    public String getStatusLabel() {
        return application == null || application.getStatus() == null ? "-" : application.getStatus();
    }

    public String getStatusTagClass() {
        if (application == null) {
            return "status-badge";
        }
        if (application.isPending()) {
            return "status-badge status-pending";
        }
        if (application.isShortlisted()) {
            return "status-badge status-shortlisted";
        }
        if (application.isRejected()) {
            return "status-badge status-rejected";
        }
        if (application.isWithdrawn()) {
            return "status-badge status-withdrawn";
        }
        return "status-badge";
    }

    public String getSubmittedAtDisplay() {
        Long createdAt = application == null ? null : application.getCreatedAtEpochMillis();
        return createdAt == null ? "-" : DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(createdAt));
    }
}
