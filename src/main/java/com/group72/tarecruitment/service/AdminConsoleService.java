package com.group72.tarecruitment.service;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.AdminCvFileView;
import com.group72.tarecruitment.model.AdminDashboardView;
import com.group72.tarecruitment.model.AdminPathStatusView;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdminConsoleService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JobRepository jobRepository;
    private final Path appHome;
    private final Path dataDir;
    private final Path storageDir;
    private final Path cvDir;
    private final Path usersFile;
    private final Path profilesFile;
    private final Path jobsFile;

    public AdminConsoleService() {
        this(
                new UserRepository(),
                new ProfileRepository(),
                new JobRepository(),
                AppConfig.getAppHome(),
                AppConfig.getDataDir(),
                AppConfig.getStorageDir(),
                AppConfig.getCvStorageDir(),
                AppConfig.resolveDataFile("users.json"),
                AppConfig.resolveDataFile("profiles.json"),
                AppConfig.resolveDataFile("jobs.json")
        );
    }

    public AdminConsoleService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            JobRepository jobRepository,
            Path appHome,
            Path dataDir,
            Path storageDir,
            Path cvDir,
            Path usersFile,
            Path profilesFile,
            Path jobsFile
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.jobRepository = jobRepository;
        this.appHome = appHome;
        this.dataDir = dataDir;
        this.storageDir = storageDir;
        this.cvDir = cvDir;
        this.usersFile = usersFile;
        this.profilesFile = profilesFile;
        this.jobsFile = jobsFile;
    }

    public AdminDashboardView buildDashboard() {
        List<User> users = userRepository.findAll().stream()
                .sorted(Comparator.comparing(user -> safeLower(user.getUsername())))
                .toList();
        List<Profile> profiles = profileRepository.findAll().stream()
                .sorted(Comparator.comparing(profile -> safeLower(profile.getName())))
                .toList();
        List<Job> jobs = jobRepository.findAll().stream()
                .sorted(Comparator.comparing(job -> safeLower(job.getTitle())))
                .toList();

        Map<String, Profile> profilesByUserId = profiles.stream()
                .collect(Collectors.toMap(Profile::getUserId, profile -> profile, (left, right) -> left));
        List<AdminCvFileView> cvFiles = listCvFiles(profilesByUserId);

        int taUserCount = (int) users.stream().filter(user -> user.getRole() == Role.TA).count();
        int moUserCount = (int) users.stream().filter(user -> user.getRole() == Role.MO).count();
        int adminUserCount = (int) users.stream().filter(user -> user.getRole() == Role.ADMIN).count();
        int openJobCount = (int) jobs.stream().filter(Job::isOpen).count();
        int profilesWithCvCount = (int) profiles.stream().filter(Profile::hasCv).count();
        long totalCvBytes = cvFiles.stream().mapToLong(AdminCvFileView::getSizeBytes).sum();

        return new AdminDashboardView(
                users,
                profiles,
                jobs,
                List.of(
                        buildPathStatus("App Home", appHome),
                        buildPathStatus("Data Directory", dataDir),
                        buildPathStatus("Storage Directory", storageDir),
                        buildPathStatus("CV Directory", cvDir),
                        buildPathStatus("users.json", usersFile),
                        buildPathStatus("profiles.json", profilesFile),
                        buildPathStatus("jobs.json", jobsFile)
                ),
                cvFiles,
                taUserCount,
                moUserCount,
                adminUserCount,
                openJobCount,
                profilesWithCvCount,
                totalCvBytes
        );
    }

    private List<AdminCvFileView> listCvFiles(Map<String, Profile> profilesByUserId) {
        if (cvDir == null || Files.notExists(cvDir) || !Files.isDirectory(cvDir)) {
            return List.of();
        }

        try (Stream<Path> pathStream = Files.list(cvDir)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(path -> safeLower(path.getFileName().toString())))
                    .map(path -> toCvFileView(path, profilesByUserId))
                    .toList();
        } catch (IOException exception) {
            return List.of();
        }
    }

    private AdminCvFileView toCvFileView(Path path, Map<String, Profile> profilesByUserId) {
        String fileName = path.getFileName().toString();
        long sizeBytes = safeSize(path);
        Instant lastModifiedAt = safeLastModified(path);
        Profile ownerProfile = profilesByUserId.values().stream()
                .filter(profile -> fileName.equals(profile.getCvPath()))
                .findFirst()
                .orElse(null);
        String ownerUserId = ownerProfile == null ? "-" : ownerProfile.getUserId();
        String ownerDisplayName = ownerProfile == null
                ? "Unlinked file"
                : (ownerProfile.getName() == null || ownerProfile.getName().isBlank() ? ownerProfile.getEmail() : ownerProfile.getName());

        return new AdminCvFileView(fileName, sizeBytes, lastModifiedAt, ownerUserId, ownerDisplayName);
    }

    private AdminPathStatusView buildPathStatus(String label, Path path) {
        boolean present = path != null && Files.exists(path);
        String detail;
        if (!present) {
            detail = "Not found yet";
        } else if (Files.isDirectory(path)) {
            detail = "Directory";
        } else if (Files.isRegularFile(path)) {
            detail = "File";
        } else {
            detail = "Present";
        }
        return new AdminPathStatusView(label, path == null ? "-" : path.toString(), present, detail);
    }

    private long safeSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException exception) {
            return 0L;
        }
    }

    private Instant safeLastModified(Path path) {
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            return lastModifiedTime.toInstant();
        } catch (IOException exception) {
            return null;
        }
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
