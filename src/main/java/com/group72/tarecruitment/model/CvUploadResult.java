package com.group72.tarecruitment.model;

import java.util.Collections;
import java.util.List;

public class CvUploadResult {
    private final boolean success;
    private final Profile profile;
    private final List<String> errors;
    private final String storedFileName;

    public CvUploadResult(boolean success, Profile profile, List<String> errors, String storedFileName) {
        this.success = success;
        this.profile = profile;
        this.errors = errors == null ? List.of() : List.copyOf(errors);
        this.storedFileName = storedFileName;
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

    public String getStoredFileName() {
        return storedFileName;
    }
}
