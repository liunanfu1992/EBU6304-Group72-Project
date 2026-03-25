package com.group72.tarecruitment.model;

import java.util.Collections;
import java.util.List;

public class JobCreateResult {
    private final boolean success;
    private final Job job;
    private final List<String> errors;

    public JobCreateResult(boolean success, Job job, List<String> errors) {
        this.success = success;
        this.job = job;
        this.errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public Job getJob() {
        return job;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
