package com.group72.tarecruitment.model;

import java.util.Collections;
import java.util.List;

public class RegistrationResult {
    private final boolean success;
    private final User user;
    private final Profile profile;
    private final List<String> errors;

    public RegistrationResult(boolean success, User user, Profile profile, List<String> errors) {
        this.success = success;
        this.user = user;
        this.profile = profile;
        this.errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
