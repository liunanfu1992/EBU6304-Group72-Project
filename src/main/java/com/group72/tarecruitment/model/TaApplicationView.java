package com.group72.tarecruitment.model;

import com.group72.tarecruitment.util.InterviewLinkPolicy;
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
        if (application.isOffered()) {
            return "status-badge status-offered";
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

    public boolean hasInterviewSchedule() {
        return application != null && application.hasInterviewSchedule();
    }

    public boolean getHasInterviewSchedule() {
        return hasInterviewSchedule();
    }

    public String getInterviewStartDisplay() {
        Long interviewStart = application == null ? null : application.getInterviewStartEpochMillis();
        return interviewStart == null ? "-" : DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(interviewStart));
    }

    public String getInterviewLocationDisplay() {
        String location = application == null ? null : application.getInterviewLocation();
        return location == null || location.isBlank() ? "-" : location;
    }

    public boolean hasInterviewLocation() {
        String location = application == null ? null : application.getInterviewLocation();
        return location != null && !location.isBlank();
    }

    public boolean getHasInterviewLocation() {
        return hasInterviewLocation();
    }

    public String getInterviewLink() {
        String link = application == null ? null : application.getInterviewLink();
        return InterviewLinkPolicy.safeDisplayLink(link);
    }

    public boolean hasInterviewLink() {
        return getInterviewLink() != null && !getInterviewLink().isBlank();
    }

    public boolean getHasInterviewLink() {
        return hasInterviewLink();
    }

    public boolean isAttendanceConfirmable() {
        return application != null
                && application.hasInterviewSchedule()
                && application.isShortlisted()
                && !application.isAttendanceConfirmed();
    }

    public boolean getAttendanceConfirmable() {
        return isAttendanceConfirmable();
    }

    public String getAttendanceLabel() {
        return application != null && application.isAttendanceConfirmed() ? "Confirmed" : "Not confirmed";
    }

    public String getAttendanceTagClass() {
        return application != null && application.isAttendanceConfirmed() ? "tag" : "tag tag-muted";
    }

    public boolean isAttendanceConfirmed() {
        return application != null && application.isAttendanceConfirmed();
    }

    public boolean getAttendanceConfirmed() {
        return isAttendanceConfirmed();
    }

    public boolean getFinalDecisionRecorded() {
        return application != null && application.isFinalDecisionMade();
    }

    public String getFinalDecisionLabel() {
        return getFinalDecisionRecorded() ? getStatusLabel() : "-";
    }

    public String getInterviewOutcomeNotesDisplay() {
        String notes = application == null ? null : application.getInterviewOutcomeNotes();
        return notes == null || notes.isBlank() ? "-" : notes;
    }

    public String getModuleCodeDisplay() {
        return job == null || job.getModuleCode() == null || job.getModuleCode().isBlank() ? "-" : job.getModuleCode();
    }

    public String getJobTitleDisplay() {
        return job == null || job.getTitle() == null || job.getTitle().isBlank() ? "Job unavailable" : job.getTitle();
    }
}
