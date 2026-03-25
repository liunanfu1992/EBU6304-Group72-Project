package com.group72.tarecruitment.model;

import java.util.Collections;
import java.util.List;

public class ProfileUpdateResult {
    private final boolean success;
    private final Profile profile;
    private final List<String> errors;

    public ProfileUpdateResult(boolean success, Profile profile, List<String> errors) {
        this.success = success;
        this.profile = profile;
        this.errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
