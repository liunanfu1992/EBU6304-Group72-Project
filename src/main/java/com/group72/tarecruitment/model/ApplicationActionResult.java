package com.group72.tarecruitment.model;

import java.util.Collections;
import java.util.List;

public class ApplicationActionResult {
    private final boolean success;
    private final Application application;
    private final List<String> errors;

    public ApplicationActionResult(boolean success, Application application, List<String> errors) {
        this.success = success;
        this.application = application;
        this.errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public Application getApplication() {
        return application;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
