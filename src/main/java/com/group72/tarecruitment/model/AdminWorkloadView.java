package com.group72.tarecruitment.model;

import java.util.List;

public class AdminWorkloadView {
    private final User taUser;
    private final Profile profile;
    private final List<Job> offeredJobs;
    private final int totalAssignedHours;

    public AdminWorkloadView(User taUser, Profile profile, List<Job> offeredJobs, int totalAssignedHours) {
        this.taUser = taUser;
        this.profile = profile;
        this.offeredJobs = offeredJobs == null ? List.of() : List.copyOf(offeredJobs);
        this.totalAssignedHours = totalAssignedHours;
    }

    public User getTaUser() {
        return taUser;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<Job> getOfferedJobs() {
        return offeredJobs;
    }

    public int getTotalAssignedHours() {
        return totalAssignedHours;
    }

    public int getOfferedJobCount() {
        return offeredJobs.size();
    }

    public String getDisplayName() {
        if (profile != null && profile.getName() != null && !profile.getName().isBlank()) {
            return profile.getName();
        }
        if (taUser != null && taUser.getUsername() != null && !taUser.getUsername().isBlank()) {
            return taUser.getUsername();
        }
        return "Unknown TA";
    }

    public String getEmail() {
        if (profile != null && profile.getEmail() != null && !profile.getEmail().isBlank()) {
            return profile.getEmail();
        }
        return taUser == null ? "" : taUser.getEmail();
    }
}
