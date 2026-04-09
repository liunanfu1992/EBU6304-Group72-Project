package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.model.ApplicationActionResult;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.MoApplicationView;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.TaApplicationView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ApplicationServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void applyToJobShouldCreatePendingApplicationForOpenJob() {
        ApplicationService service = buildService();

        ApplicationActionResult result = service.applyToJob("ta-1", "job-1");

        assertTrue(result.isSuccess());
        assertEquals(Application.STATUS_PENDING, result.getApplication().getStatus());
        assertEquals(List.of("job-1"), service.listApplicationsByTaUser("ta-1").stream().map(Application::getJobId).toList());
    }

    @Test
    void applyToJobShouldRejectDuplicateAndClosedJobApplications() {
        ApplicationService service = buildService();

        assertTrue(service.applyToJob("ta-1", "job-1").isSuccess());

        ApplicationActionResult duplicateResult = service.applyToJob("ta-1", "job-1");
        ApplicationActionResult closedJobResult = service.applyToJob("ta-1", "job-2");

        assertFalse(duplicateResult.isSuccess());
        assertTrue(duplicateResult.getErrors().contains("You have already applied for this job."));
        assertFalse(closedJobResult.isSuccess());
        assertTrue(closedJobResult.getErrors().contains("Job is not available for application."));
    }

    @Test
    void withdrawApplicationShouldOnlyAllowPendingOwnerApplication() {
        ApplicationService service = buildService();
        ApplicationActionResult applyResult = service.applyToJob("ta-1", "job-1");

        ApplicationActionResult withdrawResult = service.withdrawApplication(applyResult.getApplication().getId(), "ta-1");
        ApplicationActionResult secondWithdrawResult = service.withdrawApplication(applyResult.getApplication().getId(), "ta-1");

        assertTrue(withdrawResult.isSuccess());
        assertEquals(Application.STATUS_WITHDRAWN, withdrawResult.getApplication().getStatus());
        assertFalse(secondWithdrawResult.isSuccess());
        assertTrue(secondWithdrawResult.getErrors().contains("Only pending applications can be withdrawn."));
    }

    @Test
    void updateApplicationStatusShouldRequireMoOwnershipAndBlockWithdrawnApplications() {
        ApplicationService service = buildService();
        ApplicationActionResult applyResult = service.applyToJob("ta-1", "job-1");

        ApplicationActionResult shortlistResult = service.updateApplicationStatus(
                applyResult.getApplication().getId(),
                "mo-1",
                Application.STATUS_SHORTLISTED
        );
        ApplicationActionResult unauthorizedResult = service.updateApplicationStatus(
                applyResult.getApplication().getId(),
                "mo-2",
                Application.STATUS_REJECTED
        );

        assertTrue(shortlistResult.isSuccess());
        assertEquals(Application.STATUS_SHORTLISTED, shortlistResult.getApplication().getStatus());
        assertFalse(unauthorizedResult.isSuccess());
        assertTrue(unauthorizedResult.getErrors().contains("Application not found."));

        ApplicationActionResult secondApplyResult = service.applyToJob("ta-2", "job-1");
        assertTrue(service.withdrawApplication(secondApplyResult.getApplication().getId(), "ta-2").isSuccess());

        ApplicationActionResult withdrawnReviewResult = service.updateApplicationStatus(
                secondApplyResult.getApplication().getId(),
                "mo-1",
                Application.STATUS_REJECTED
        );

        assertFalse(withdrawnReviewResult.isSuccess());
        assertTrue(withdrawnReviewResult.getErrors().contains("Withdrawn applications can no longer be processed."));
    }

    @Test
    void listApplicationsForMoUserShouldReturnOnlyOwnedJobApplications() {
        ApplicationService service = buildService();

        assertTrue(service.applyToJob("ta-1", "job-1").isSuccess());
        assertTrue(service.applyToJob("ta-2", "job-3").isSuccess());

        List<String> ownedJobIds = service.listApplicationsForMoUser("mo-1").stream()
                .map(Application::getJobId)
                .toList();

        assertEquals(List.of("job-1"), ownedJobIds);
    }

    @Test
    void listTaApplicationViewsShouldJoinJobAndMoMetadata() {
        ApplicationService service = buildService();

        assertTrue(service.applyToJob("ta-1", "job-1").isSuccess());

        List<TaApplicationView> views = service.listTaApplicationViews("ta-1");

        assertEquals(1, views.size());
        assertEquals("Open Job", views.get(0).getJob().getTitle());
        assertEquals("mo-smith", views.get(0).getModuleOwnerDisplayName());
        assertEquals("smith@example.com", views.get(0).getModuleOwnerEmail());
        assertEquals(Application.STATUS_PENDING, views.get(0).getStatusLabel());
        assertTrue(views.get(0).isWithdrawable());
    }

    @Test
    void findTaApplicationViewForJobShouldReturnOwnedApplicationAndReflectWithdrawState() {
        ApplicationService service = buildService();

        ApplicationActionResult applyResult = service.applyToJob("ta-1", "job-1");
        assertTrue(applyResult.isSuccess());
        assertTrue(service.findTaApplicationViewForJob("ta-1", "job-1").isPresent());

        assertTrue(service.withdrawApplication(applyResult.getApplication().getId(), "ta-1").isSuccess());

        TaApplicationView withdrawnView = service.findTaApplicationViewForJob("ta-1", "job-1").orElseThrow();
        assertEquals(Application.STATUS_WITHDRAWN, withdrawnView.getStatusLabel());
        assertFalse(withdrawnView.isWithdrawable());
    }

    @Test
    void moApplicationViewsShouldJoinCandidateProfileData() {
        ApplicationService service = buildService();

        assertTrue(service.applyToJob("ta-1", "job-1").isSuccess());

        List<MoApplicationView> views = service.listMoApplicationViews("mo-1");

        assertEquals(1, views.size());
        assertEquals("Alice", views.get(0).getCandidateDisplayName());
        assertEquals("20260001", views.get(0).getStudentIdDisplay());
        assertEquals("Computer Science", views.get(0).getMajorDisplay());
        assertTrue(views.get(0).hasCv());
        assertTrue(service.findOwnedApplicationView(views.get(0).getApplication().getId(), "mo-1").isPresent());
    }

    @Test
    void moApplicationViewShouldLockReviewActionsAfterTaWithdrawal() {
        ApplicationService service = buildService();

        ApplicationActionResult applyResult = service.applyToJob("ta-1", "job-1");
        assertTrue(applyResult.isSuccess());
        assertTrue(service.withdrawApplication(applyResult.getApplication().getId(), "ta-1").isSuccess());

        MoApplicationView view = service.findOwnedApplicationView(applyResult.getApplication().getId(), "mo-1").orElseThrow();

        assertTrue(view.isReviewLocked());
        assertFalse(view.getCanShortlist());
        assertFalse(view.getCanReject());
    }

    @Test
    void updateApplicationStatusShouldRemainAvailableForClosedOwnedJobs() {
        UserRepository userRepository = new UserRepository(tempDir.resolve("users-closed-job.json"));
        JobRepository jobRepository = new JobRepository(tempDir.resolve("jobs-closed-job.json"));
        ApplicationRepository applicationRepository = new ApplicationRepository(tempDir.resolve("applications-closed-job.json"));
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles-closed-job.json"));

        userRepository.save(new User("ta-1", "ta-alice", "", Role.TA, "alice@example.com"));
        userRepository.save(new User("mo-1", "mo-smith", "", Role.MO, "smith@example.com"));
        profileRepository.save(new Profile(
                "ta-1",
                "Alice",
                "20260001",
                "Computer Science",
                "alice@example.com",
                List.of("Java"),
                List.of("Mentoring"),
                "ta-1-resume.pdf"
        ));

        Job job = new Job("job-1", "Open Job", "CS101", "Open job", List.of("Java"), 6, "mo-1", Job.STATUS_OPEN);
        jobRepository.save(job);

        ApplicationService service = new ApplicationService(applicationRepository, jobRepository, userRepository, profileRepository);
        ApplicationActionResult applyResult = service.applyToJob("ta-1", "job-1");
        assertTrue(applyResult.isSuccess());

        job.setStatus(Job.STATUS_CLOSED);
        jobRepository.save(job);

        ApplicationActionResult reviewResult = service.updateApplicationStatus(
                applyResult.getApplication().getId(),
                "mo-1",
                Application.STATUS_SHORTLISTED
        );

        assertTrue(reviewResult.isSuccess());
        MoApplicationView view = service.findOwnedApplicationView(applyResult.getApplication().getId(), "mo-1").orElseThrow();
        assertEquals(Application.STATUS_SHORTLISTED, view.getStatusLabel());
        assertTrue(view.isJobClosed());
        assertFalse(view.getCanShortlist());
        assertTrue(view.getCanReject());
        assertFalse(view.isReviewLocked());
    }

    private ApplicationService buildService() {
        UserRepository userRepository = new UserRepository(tempDir.resolve("users.json"));
        JobRepository jobRepository = new JobRepository(tempDir.resolve("jobs.json"));
        ApplicationRepository applicationRepository = new ApplicationRepository(tempDir.resolve("applications.json"));
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles.json"));

        userRepository.save(new User("ta-1", "ta-alice", "", Role.TA, "alice@example.com"));
        userRepository.save(new User("ta-2", "ta-bob", "", Role.TA, "bob@example.com"));
        userRepository.save(new User("mo-1", "mo-smith", "", Role.MO, "smith@example.com"));
        userRepository.save(new User("mo-2", "mo-jones", "", Role.MO, "jones@example.com"));

        profileRepository.save(new Profile(
                "ta-1",
                "Alice",
                "20260001",
                "Computer Science",
                "alice@example.com",
                List.of("Java"),
                List.of("Mentoring"),
                "ta-1-resume.pdf"
        ));
        profileRepository.save(new Profile(
                "ta-2",
                "Bob",
                "20260002",
                "Mathematics",
                "bob@example.com",
                List.of("Python"),
                List.of(),
                null
        ));

        jobRepository.save(new Job("job-1", "Open Job", "CS101", "Open job", List.of("Java"), 6, "mo-1", Job.STATUS_OPEN));
        jobRepository.save(new Job("job-2", "Closed Job", "CS102", "Closed job", List.of("Python"), 5, "mo-1", Job.STATUS_CLOSED));
        jobRepository.save(new Job("job-3", "Other MO Job", "CS103", "Other owner job", List.of("Communication"), 4, "mo-2", Job.STATUS_OPEN));

        return new ApplicationService(applicationRepository, jobRepository, userRepository, profileRepository);
    }
}
