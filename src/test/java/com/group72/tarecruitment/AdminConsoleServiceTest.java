package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.group72.tarecruitment.model.AdminDashboardView;
import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.AdminConsoleService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AdminConsoleServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void buildDashboardShouldSummarizeStoredRecordsAndCvFiles() throws Exception {
        Path appHome = tempDir.resolve("app-home");
        Path dataDir = appHome.resolve("data");
        Path storageDir = appHome.resolve("storage");
        Path cvDir = storageDir.resolve("cv");
        Path usersFile = dataDir.resolve("users.json");
        Path profilesFile = dataDir.resolve("profiles.json");
        Path jobsFile = dataDir.resolve("jobs.json");
        Path applicationsFile = dataDir.resolve("applications.json");

        UserRepository userRepository = new UserRepository(usersFile);
        ProfileRepository profileRepository = new ProfileRepository(profilesFile);
        JobRepository jobRepository = new JobRepository(jobsFile);
        ApplicationRepository applicationRepository = new ApplicationRepository(applicationsFile);

        userRepository.save(new User("ta-1", "ta-demo", "", Role.TA, "ta@example.com"));
        userRepository.save(new User("mo-1", "mo-demo", "", Role.MO, "mo@example.com"));
        userRepository.save(new User("admin-1", "admin-demo", "", Role.ADMIN, "admin@example.com"));

        profileRepository.save(new Profile(
                "ta-1",
                "Alice",
                "20260001",
                "Computer Science",
                "alice@example.com",
                List.of("Java", "Communication"),
                List.of(),
                "ta-1-resume.pdf"
        ));
        profileRepository.save(new Profile(
                "ta-2",
                "Bob",
                "20260002",
                "Software Engineering",
                "bob@example.com",
                List.of("Python"),
                List.of(),
                null
        ));

        jobRepository.save(new Job("job-1", "Programming TA", "CS101", "Support labs", List.of("Java"), 8, "mo-1", "OPEN"));
        jobRepository.save(new Job("job-2", "Data TA", "CS102", "Support tutorials", List.of("Python"), 6, "mo-1", "CLOSED"));
        jobRepository.save(new Job("job-3", "Project TA", "CS103", "Support projects", List.of("Planning"), 10, "mo-1", "OPEN"));
        applicationRepository.save(new Application("app-1", "ta-1", "job-1", Application.STATUS_OFFERED, 1L, 2L));
        applicationRepository.save(new Application("app-2", "ta-1", "job-3", Application.STATUS_OFFERED, 1L, 2L));
        applicationRepository.save(new Application("app-3", "ta-1", "job-2", Application.STATUS_REJECTED, 1L, 2L));
        applicationRepository.save(new Application("app-4", "ta-1", "missing-job", Application.STATUS_OFFERED, 1L, 2L));
        applicationRepository.save(new Application("app-5", "", "job-1", Application.STATUS_OFFERED, 1L, 2L));

        Files.createDirectories(cvDir);
        Files.writeString(cvDir.resolve("ta-1-resume.pdf"), "resume-content");

        AdminConsoleService service = new AdminConsoleService(
                userRepository,
                profileRepository,
                jobRepository,
                applicationRepository,
                appHome,
                dataDir,
                storageDir,
                cvDir,
                usersFile,
                profilesFile,
                jobsFile,
                applicationsFile
        );

        AdminDashboardView dashboard = service.buildDashboard();

        assertEquals(3, dashboard.getUserCount());
        assertEquals(1, dashboard.getTaUserCount());
        assertEquals(1, dashboard.getMoUserCount());
        assertEquals(1, dashboard.getAdminUserCount());
        assertEquals(2, dashboard.getProfileCount());
        assertEquals(1, dashboard.getProfilesWithCvCount());
        assertEquals(3, dashboard.getJobCount());
        assertEquals(2, dashboard.getOpenJobCount());
        assertEquals(1, dashboard.getCvFileCount());
        assertEquals(1, dashboard.getOfferedTaCount());
        assertEquals(18, dashboard.getTotalAssignedHours());
        assertEquals(18, dashboard.getAverageAssignedHours());
        assertEquals(1, dashboard.getHighLoadTaCount());
        assertEquals(0, dashboard.getBalancedLoadTaCount());
        assertEquals(0, dashboard.getLightLoadTaCount());
        assertEquals("Alice", dashboard.getWorkloadRows().get(0).getDisplayName());
        assertEquals(2, dashboard.getWorkloadRows().get(0).getOfferedJobCount());
        assertEquals("High", dashboard.getWorkloadRows().get(0).getLoadBandLabel());
        assertTrue(dashboard.getWorkloadRows().get(0).getAssignedJobSummary().contains("Programming TA"));
        assertTrue(dashboard.getWorkloadRows().get(0).getAssignedJobSummary().contains("Project TA"));
        assertEquals("Alice", dashboard.getCvFiles().get(0).getOwnerDisplayName());
        assertTrue(dashboard.getPathStatuses().stream().allMatch(pathStatus -> pathStatus.isPresent()));
    }

    @Test
    void buildDashboardShouldHandleMissingCvDirectory() {
        Path appHome = tempDir.resolve("app-home");
        Path dataDir = appHome.resolve("data");
        Path storageDir = appHome.resolve("storage");
        Path cvDir = storageDir.resolve("cv");
        Path usersFile = dataDir.resolve("users.json");
        Path profilesFile = dataDir.resolve("profiles.json");
        Path jobsFile = dataDir.resolve("jobs.json");
        Path applicationsFile = dataDir.resolve("applications.json");

        AdminConsoleService service = new AdminConsoleService(
                new UserRepository(usersFile),
                new ProfileRepository(profilesFile),
                new JobRepository(jobsFile),
                new ApplicationRepository(applicationsFile),
                appHome,
                dataDir,
                storageDir,
                cvDir,
                usersFile,
                profilesFile,
                jobsFile,
                applicationsFile
        );

        AdminDashboardView dashboard = service.buildDashboard();

        assertEquals(0, dashboard.getCvFileCount());
        assertTrue(dashboard.getPathStatuses().stream().anyMatch(pathStatus -> !pathStatus.isPresent()));
    }
}
