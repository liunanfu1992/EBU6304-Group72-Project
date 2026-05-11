package com.group72.tarecruitment.model;

import com.group72.tarecruitment.util.InterviewLinkPolicy;
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

    public int getMatchPercent() {
        List<String> requiredSkills = job == null ? List.of() : SkillCatalog.extractPredefinedSkills(job.getRequiredSkills());
        if (requiredSkills.isEmpty()) {
            return 100;
        }
        return getMatchedSkills().size() * 100 / requiredSkills.size();
    }

    public String getMatchLabel() {
        if (getMissingSkills().isEmpty()) {
            return "Strong candidate";
        }
        if (getMatchPercent() >= 50) {
            return "Promising candidate";
        }
        if (getMatchPercent() > 0) {
            return "Partial candidate";
        }
        return "Low match";
    }

    public String getMatchTone() {
        if (getMissingSkills().isEmpty()) {
            return "success";
        }
        if (getMatchPercent() >= 50) {
            return "info";
        }
        if (getMatchPercent() > 0) {
            return "warning";
        }
        return "muted";
    }

    public String getMatchEvidenceSummary() {
        List<String> requiredSkills = job == null ? List.of() : SkillCatalog.extractPredefinedSkills(job.getRequiredSkills());
        if (requiredSkills.isEmpty()) {
            return "No predefined required skills are attached to this job, so the candidate is treated as a complete structured match.";
        }
        return getMatchedSkills().size() + " of " + requiredSkills.size()
                + " predefined required skills matched; " + getMissingSkills().size() + " missing.";
    }

    public boolean isMatchedSkill(String skill) {
        return containsSkillIgnoreCase(getMatchedSkills(), skill);
    }

    public boolean isMissingSkill(String skill) {
        return containsSkillIgnoreCase(getMissingSkills(), skill);
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
        return application == null || application.isWithdrawn() || application.isFinalDecisionMade();
    }

    public boolean getCanShortlist() {
        return application != null && !application.isWithdrawn() && !application.isFinalDecisionMade() && !application.isShortlisted();
    }

    public boolean getCanReject() {
        return application != null && !application.isWithdrawn() && !application.isFinalDecisionMade() && !application.isRejected();
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

    public String getInterviewLocationInputValue() {
        String location = application == null ? null : application.getInterviewLocation();
        return location == null ? "" : location;
    }

    public String getInterviewLinkDisplay() {
        String link = application == null ? null : application.getInterviewLink();
        String safeLink = InterviewLinkPolicy.safeDisplayLink(link);
        return safeLink.isBlank() ? "-" : safeLink;
    }

    public String getInterviewLinkInputValue() {
        String link = application == null ? null : application.getInterviewLink();
        return link == null ? "" : link;
    }

    public String getAttendanceLabel() {
        return application != null && application.isAttendanceConfirmed() ? "Confirmed" : "Not confirmed";
    }

    public boolean getCanScheduleInterview() {
        return application != null && application.isShortlisted() && !application.isFinalDecisionMade();
    }

    public boolean getCanRecordOutcome() {
        return application != null
                && application.hasInterviewSchedule()
                && application.isShortlisted()
                && !application.isWithdrawn()
                && !application.isFinalDecisionMade();
    }

    public boolean getFinalDecisionRecorded() {
        return application != null && application.isFinalDecisionMade();
    }

    public String getFinalDecisionLabel() {
        return getFinalDecisionRecorded() ? getStatusLabel() : "-";
    }

    public String getFinalDecisionAtDisplay() {
        Long finalDecisionAt = application == null ? null : application.getFinalDecisionAtEpochMillis();
        return finalDecisionAt == null ? "-" : DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(finalDecisionAt));
    }

    public String getInterviewOutcomeNotesDisplay() {
        String notes = application == null ? null : application.getInterviewOutcomeNotes();
        return notes == null || notes.isBlank() ? "-" : notes;
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
