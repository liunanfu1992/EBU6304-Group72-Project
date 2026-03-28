package com.group72.tarecruitment.model;

import java.util.List;

public class AdminDashboardView {
    private final List<User> users;
    private final List<Profile> profiles;
    private final List<Job> jobs;
    private final List<AdminPathStatusView> pathStatuses;
    private final List<AdminCvFileView> cvFiles;
    private final int taUserCount;
    private final int moUserCount;
    private final int adminUserCount;
    private final int openJobCount;
    private final int profilesWithCvCount;
    private final long totalCvBytes;

    public AdminDashboardView(
            List<User> users,
            List<Profile> profiles,
            List<Job> jobs,
            List<AdminPathStatusView> pathStatuses,
            List<AdminCvFileView> cvFiles,
            int taUserCount,
            int moUserCount,
            int adminUserCount,
            int openJobCount,
            int profilesWithCvCount,
            long totalCvBytes
    ) {
        this.users = List.copyOf(users);
        this.profiles = List.copyOf(profiles);
        this.jobs = List.copyOf(jobs);
        this.pathStatuses = List.copyOf(pathStatuses);
        this.cvFiles = List.copyOf(cvFiles);
        this.taUserCount = taUserCount;
        this.moUserCount = moUserCount;
        this.adminUserCount = adminUserCount;
        this.openJobCount = openJobCount;
        this.profilesWithCvCount = profilesWithCvCount;
        this.totalCvBytes = totalCvBytes;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public List<AdminPathStatusView> getPathStatuses() {
        return pathStatuses;
    }

    public List<AdminCvFileView> getCvFiles() {
        return cvFiles;
    }

    public int getUserCount() {
        return users.size();
    }

    public int getProfileCount() {
        return profiles.size();
    }

    public int getJobCount() {
        return jobs.size();
    }

    public int getCvFileCount() {
        return cvFiles.size();
    }

    public int getTaUserCount() {
        return taUserCount;
    }

    public int getMoUserCount() {
        return moUserCount;
    }

    public int getAdminUserCount() {
        return adminUserCount;
    }

    public int getOpenJobCount() {
        return openJobCount;
    }

    public int getProfilesWithCvCount() {
        return profilesWithCvCount;
    }

    public long getTotalCvBytes() {
        return totalCvBytes;
    }

    public String getTotalCvSizeDisplay() {
        if (totalCvBytes < 1024) {
            return totalCvBytes + " B";
        }
        if (totalCvBytes < 1024 * 1024) {
            return String.format("%.1f KB", totalCvBytes / 1024.0);
        }
        return String.format("%.2f MB", totalCvBytes / (1024.0 * 1024.0));
    }
}
