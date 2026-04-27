package com.group72.tarecruitment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_WITHDRAWN = "WITHDRAWN";
    public static final String STATUS_SHORTLISTED = "SHORTLISTED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_OFFERED = "OFFERED";

    private String id;
    private String taUserId;
    private String jobId;
    private String status;
    private Long createdAtEpochMillis;
    private Long updatedAtEpochMillis;
    private Long interviewStartEpochMillis;
    private String interviewLocation;
    private String interviewLink;
    private Boolean attendanceConfirmed;
    private Long attendanceConfirmedAtEpochMillis;
    private String interviewOutcomeNotes;
    private Long finalDecisionAtEpochMillis;

    public Application() {
    }

    public Application(
            String id,
            String taUserId,
            String jobId,
            String status,
            Long createdAtEpochMillis,
            Long updatedAtEpochMillis
    ) {
        this.id = id;
        this.taUserId = taUserId;
        this.jobId = jobId;
        this.status = status;
        this.createdAtEpochMillis = createdAtEpochMillis;
        this.updatedAtEpochMillis = updatedAtEpochMillis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaUserId() {
        return taUserId;
    }

    public void setTaUserId(String taUserId) {
        this.taUserId = taUserId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedAtEpochMillis() {
        return createdAtEpochMillis;
    }

    public void setCreatedAtEpochMillis(Long createdAtEpochMillis) {
        this.createdAtEpochMillis = createdAtEpochMillis;
    }

    public Long getUpdatedAtEpochMillis() {
        return updatedAtEpochMillis;
    }

    public void setUpdatedAtEpochMillis(Long updatedAtEpochMillis) {
        this.updatedAtEpochMillis = updatedAtEpochMillis;
    }

    public Long getInterviewStartEpochMillis() {
        return interviewStartEpochMillis;
    }

    public void setInterviewStartEpochMillis(Long interviewStartEpochMillis) {
        this.interviewStartEpochMillis = interviewStartEpochMillis;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public String getInterviewLink() {
        return interviewLink;
    }

    public void setInterviewLink(String interviewLink) {
        this.interviewLink = interviewLink;
    }

    public Boolean getAttendanceConfirmed() {
        return attendanceConfirmed;
    }

    public void setAttendanceConfirmed(Boolean attendanceConfirmed) {
        this.attendanceConfirmed = attendanceConfirmed;
    }

    public Long getAttendanceConfirmedAtEpochMillis() {
        return attendanceConfirmedAtEpochMillis;
    }

    public void setAttendanceConfirmedAtEpochMillis(Long attendanceConfirmedAtEpochMillis) {
        this.attendanceConfirmedAtEpochMillis = attendanceConfirmedAtEpochMillis;
    }

    public String getInterviewOutcomeNotes() {
        return interviewOutcomeNotes;
    }

    public void setInterviewOutcomeNotes(String interviewOutcomeNotes) {
        this.interviewOutcomeNotes = interviewOutcomeNotes;
    }

    public Long getFinalDecisionAtEpochMillis() {
        return finalDecisionAtEpochMillis;
    }

    public void setFinalDecisionAtEpochMillis(Long finalDecisionAtEpochMillis) {
        this.finalDecisionAtEpochMillis = finalDecisionAtEpochMillis;
    }

    @JsonIgnore
    public boolean isPending() {
        return STATUS_PENDING.equalsIgnoreCase(status);
    }

    @JsonIgnore
    public boolean isWithdrawn() {
        return STATUS_WITHDRAWN.equalsIgnoreCase(status);
    }

    @JsonIgnore
    public boolean isShortlisted() {
        return STATUS_SHORTLISTED.equalsIgnoreCase(status);
    }

    @JsonIgnore
    public boolean isRejected() {
        return STATUS_REJECTED.equalsIgnoreCase(status);
    }

    @JsonIgnore
    public boolean isOffered() {
        return STATUS_OFFERED.equalsIgnoreCase(status);
    }

    @JsonIgnore
    public boolean hasInterviewSchedule() {
        return interviewStartEpochMillis != null;
    }

    public boolean isAttendanceConfirmed() {
        return Boolean.TRUE.equals(attendanceConfirmed);
    }

    @JsonIgnore
    public boolean isFinalDecisionMade() {
        return isOffered() || isRejected();
    }
}
