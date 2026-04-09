package com.group72.tarecruitment.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TaApplicationView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private final Application application;
    private final Job job;
    private final String moduleOwnerDisplayName;
    private final String moduleOwnerEmail;

    public TaApplicationView(Application application, Job job, String moduleOwnerDisplayName, String moduleOwnerEmail) {
        this.application = application;
        this.job = job;
        this.moduleOwnerDisplayName = moduleOwnerDisplayName;
        this.moduleOwnerEmail = moduleOwnerEmail;
    }

    public Application getApplication() {
        return application;
    }

    public Job getJob() {
        return job;
    }

    public String getModuleOwnerDisplayName() {
        return moduleOwnerDisplayName;
    }

    public String getModuleOwnerEmail() {
        return moduleOwnerEmail;
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

    public boolean isWithdrawable() {
        return application != null && application.isPending();
    }
}
