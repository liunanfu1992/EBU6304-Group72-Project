package com.group72.tarecruitment.model;

import java.util.List;
import java.util.stream.Collectors;

public class AdminWorkloadView {
    public static final int LIGHT_LOAD_THRESHOLD = 8;
    public static final int HIGH_LOAD_THRESHOLD = 15;
    public static final int SCHOOL_HOUR_LIMIT = 15;

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

    public boolean isLightLoad() {
        return totalAssignedHours <= LIGHT_LOAD_THRESHOLD;
    }

    public boolean isHighLoad() {
        return totalAssignedHours > HIGH_LOAD_THRESHOLD;
    }

    public boolean isOverloaded() {
        return totalAssignedHours > SCHOOL_HOUR_LIMIT;
    }

    public int getOverloadHours() {
        return Math.max(0, totalAssignedHours - SCHOOL_HOUR_LIMIT);
    }

    public String getOverloadSummary() {
        if (!isOverloaded()) {
            return "Within " + SCHOOL_HOUR_LIMIT + "h limit";
        }
        return getOverloadHours() + "h over " + SCHOOL_HOUR_LIMIT + "h limit";
    }

    public boolean isBalancedLoad() {
        return !isLightLoad() && !isHighLoad();
    }

    public String getLoadBandLabel() {
        if (isHighLoad()) {
            return "High";
        }
        if (isLightLoad()) {
            return "Light";
        }
        return "Balanced";
    }

    public String getLoadBandTagClass() {
        if (isHighLoad()) {
            return "status-badge status-rejected";
        }
        if (isLightLoad()) {
            return "status-badge status-shortlisted";
        }
        return "status-badge status-offered";
    }

    public String getAssignedJobSummary() {
        if (offeredJobs.isEmpty()) {
            return "-";
        }
        return offeredJobs.stream()
                .map(job -> {
                    String title = job.getTitle() == null || job.getTitle().isBlank() ? "Untitled job" : job.getTitle();
                    String module = job.getModuleCode() == null || job.getModuleCode().isBlank() ? "-" : job.getModuleCode();
                    Integer weeklyHours = job.getWeeklyHours();
                    return title + " (" + module + ", " + (weeklyHours == null ? 0 : weeklyHours) + "h)";
                })
                .collect(Collectors.joining(", "));
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
