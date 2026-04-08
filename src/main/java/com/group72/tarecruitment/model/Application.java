package com.group72.tarecruitment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_WITHDRAWN = "WITHDRAWN";
    public static final String STATUS_SHORTLISTED = "SHORTLISTED";
    public static final String STATUS_REJECTED = "REJECTED";

    private String id;
    private String taUserId;
    private String jobId;
    private String status;
    private Long createdAtEpochMillis;
    private Long updatedAtEpochMillis;

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
}
