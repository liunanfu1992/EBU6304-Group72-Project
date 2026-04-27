package com.group72.tarecruitment.model;

import com.group72.tarecruitment.util.SkillCatalog;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public List<String> getProfileSkills() {
        return profile == null ? List.of() : profile.getAllSkills();
    }

    public List<String> getPredefinedProfileSkills() {
        return SkillCatalog.extractPredefinedSkills(getProfileSkills());
    }

    public List<String> getMatchedSkills() {
        if (job == null || job.getRequiredSkills() == null || job.getRequiredSkills().isEmpty()) {
            return List.of();
        }

        List<String> matched = new ArrayList<>();
        for (String requiredSkill : job.getRequiredSkills()) {
            if (containsSkillIgnoreCase(getProfileSkills(), requiredSkill)) {
                matched.add(requiredSkill);
            }
        }
        return matched;
    }

    public List<String> getMissingSkills() {
        if (job == null || job.getRequiredSkills() == null || job.getRequiredSkills().isEmpty()) {
            return List.of();
        }

        List<String> missing = new ArrayList<>();
        for (String requiredSkill : job.getRequiredSkills()) {
            if (!containsSkillIgnoreCase(getProfileSkills(), requiredSkill)) {
                missing.add(requiredSkill);
            }
        }
        return missing;
    }

    public boolean hasCv() {
        return profile != null && profile.hasCv();
    }

    public boolean getHasCv() {
        return hasCv();
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

    public boolean isReviewLocked() {
        return application == null || application.isWithdrawn() || application.isOffered();
    }

    public boolean getCanShortlist() {
        return application != null && !application.isWithdrawn() && !application.isOffered() && !application.isShortlisted();
    }

    public boolean getCanReject() {
        return application != null && !application.isWithdrawn() && !application.isOffered() && !application.isRejected();
    }

    public boolean isJobClosed() {
        return job != null && job.isClosed();
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

    public String getInterviewStartInputValue() {
        Long interviewStart = application == null ? null : application.getInterviewStartEpochMillis();
        return interviewStart == null
                ? ""
                : DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                        .withZone(ZoneId.systemDefault())
                        .format(Instant.ofEpochMilli(interviewStart));
    }

    public String getInterviewLocationDisplay() {
        String location = application == null ? null : application.getInterviewLocation();
        return location == null || location.isBlank() ? "-" : location;
    }

    public String getInterviewLinkDisplay() {
        String link = application == null ? null : application.getInterviewLink();
        return link == null || link.isBlank() ? "-" : link;
    }

    public String getAttendanceLabel() {
        return application != null && application.isAttendanceConfirmed() ? "Confirmed" : "Not confirmed";
    }

    public boolean getCanScheduleInterview() {
        return application != null && application.isShortlisted();
    }

    public boolean getCanRecordOutcome() {
        return application != null
                && application.hasInterviewSchedule()
                && !application.isWithdrawn()
                && !application.isFinalDecisionMade();
    }

    private boolean containsSkillIgnoreCase(List<String> skills, String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return false;
        }

        String normalizedCandidate = candidate.trim().toLowerCase(Locale.ROOT);
        for (String skill : skills) {
            if (skill != null && normalizedCandidate.equals(skill.trim().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
