package com.group72.tarecruitment.service;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.AdminWorkloadView;
import com.group72.tarecruitment.model.AdminCvFileView;
import com.group72.tarecruitment.model.AdminDashboardView;
import com.group72.tarecruitment.model.AdminPathStatusView;
import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
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
    private final ApplicationRepository applicationRepository;
    private final Path appHome;
    private final Path dataDir;
    private final Path storageDir;
    private final Path cvDir;
    private final Path usersFile;
    private final Path profilesFile;
    private final Path jobsFile;
    private final Path applicationsFile;

    public AdminConsoleService() {
        this(
                new UserRepository(),
                new ProfileRepository(),
                new JobRepository(),
                new ApplicationRepository(),
                AppConfig.getAppHome(),
                AppConfig.getDataDir(),
                AppConfig.getStorageDir(),
                AppConfig.getCvStorageDir(),
                AppConfig.resolveDataFile("users.json"),
                AppConfig.resolveDataFile("profiles.json"),
                AppConfig.resolveDataFile("jobs.json"),
                AppConfig.resolveDataFile("applications.json")
        );
    }

    public AdminConsoleService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            Path appHome,
            Path dataDir,
            Path storageDir,
            Path cvDir,
            Path usersFile,
            Path profilesFile,
            Path jobsFile,
            Path applicationsFile
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.appHome = appHome;
        this.dataDir = dataDir;
        this.storageDir = storageDir;
        this.cvDir = cvDir;
        this.usersFile = usersFile;
        this.profilesFile = profilesFile;
        this.jobsFile = jobsFile;
        this.applicationsFile = applicationsFile;
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
        Map<String, User> usersById = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user, (left, right) -> left));
        Map<String, Job> jobsById = jobs.stream()
                .collect(Collectors.toMap(Job::getId, job -> job, (left, right) -> left));
        List<AdminCvFileView> cvFiles = listCvFiles(profilesByUserId);
        List<AdminWorkloadView> workloadRows = buildWorkloadRows(usersById, profilesByUserId, jobsById);

        int taUserCount = (int) users.stream().filter(user -> user.getRole() == Role.TA).count();
        int moUserCount = (int) users.stream().filter(user -> user.getRole() == Role.MO).count();
        int adminUserCount = (int) users.stream().filter(user -> user.getRole() == Role.ADMIN).count();
        int openJobCount = (int) jobs.stream().filter(Job::isOpen).count();
        int profilesWithCvCount = (int) profiles.stream().filter(Profile::hasCv).count();
        long totalCvBytes = cvFiles.stream().mapToLong(AdminCvFileView::getSizeBytes).sum();
        int totalAssignedHours = workloadRows.stream().mapToInt(AdminWorkloadView::getTotalAssignedHours).sum();

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
                        buildPathStatus("jobs.json", jobsFile),
                        buildPathStatus("applications.json", applicationsFile)
                ),
                cvFiles,
                workloadRows,
                taUserCount,
                moUserCount,
                adminUserCount,
                openJobCount,
                profilesWithCvCount,
                totalCvBytes,
                totalAssignedHours
        );
    }

    private List<AdminWorkloadView> buildWorkloadRows(
            Map<String, User> usersById,
            Map<String, Profile> profilesByUserId,
            Map<String, Job> jobsById
    ) {
        Map<String, List<Job>> offeredJobsByTa = applicationRepository.findAll().stream()
                .filter(Application::isOffered)
                .collect(Collectors.groupingBy(
                        Application::getTaUserId,
                        Collectors.mapping(application -> jobsById.get(application.getJobId()), Collectors.toList())
                ));

        return offeredJobsByTa.entrySet().stream()
                .map(entry -> toWorkloadView(entry.getKey(), entry.getValue(), usersById, profilesByUserId))
                .sorted(Comparator.comparing(AdminWorkloadView::getDisplayName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private AdminWorkloadView toWorkloadView(
            String taUserId,
            List<Job> offeredJobs,
            Map<String, User> usersById,
            Map<String, Profile> profilesByUserId
    ) {
        List<Job> safeJobs = offeredJobs.stream()
                .filter(job -> job != null)
                .sorted(Comparator.comparing(job -> safeLower(job.getTitle())))
                .toList();
        int totalHours = safeJobs.stream()
                .map(Job::getWeeklyHours)
                .filter(hours -> hours != null)
                .mapToInt(Integer::intValue)
                .sum();
        return new AdminWorkloadView(usersById.get(taUserId), profilesByUserId.get(taUserId), safeJobs, totalHours);
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
