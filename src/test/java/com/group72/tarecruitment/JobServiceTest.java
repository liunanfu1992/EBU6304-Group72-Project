package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.JobCreateResult;
import com.group72.tarecruitment.model.JobCandidateView;
import com.group72.tarecruitment.model.JobMatchView;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.TaJobView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.JobService;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JobServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void listMatchesForProfileShouldSortBySkillCoverage() {
        JobService service = new JobService(
                new JobRepository(tempDir.resolve("jobs.json")),
                new ProfileRepository(tempDir.resolve("profiles.json"))
        );

        assertTrue(service.createJob(
                "Programming TA",
                "CS101",
                "Support labs",
                new String[]{"Java", "Communication"},
                "",
                "8",
                "mo-1"
        ).isSuccess());
        assertTrue(service.createJob(
                "Data TA",
                "CS202",
                "Support tutorials",
                new String[]{"Java", "Data Structures"},
                "",
                "6",
                "mo-1"
        ).isSuccess());
        assertTrue(service.createJob(
                "Presentation TA",
                "CS303",
                "Guide presentations",
                new String[]{"Presentation"},
                "",
                "4",
                "mo-1"
        ).isSuccess());

        Profile profile = new Profile();
        profile.setSelectedSkills(List.of("Java", "Communication"));

        List<JobMatchView> matches = service.listMatchesForProfile(profile);

        assertEquals(3, matches.size());
        assertEquals("Programming TA", matches.get(0).getJob().getTitle());
        assertEquals(100, matches.get(0).getMatchPercent());
        assertEquals("Data TA", matches.get(1).getJob().getTitle());
        assertEquals(50, matches.get(1).getMatchPercent());
        assertEquals(List.of("Data Structures"), matches.get(1).getMissingSkills());
        assertEquals("Presentation TA", matches.get(2).getJob().getTitle());
        assertEquals(0, matches.get(2).getMatchPercent());
    }

    @Test
    void createJobShouldNormalizeSkillsAndValidateWeeklyHours() {
        JobService service = new JobService(
                new JobRepository(tempDir.resolve("jobs.json")),
                new ProfileRepository(tempDir.resolve("profiles.json"))
        );

        JobCreateResult successResult = service.createJob(
                "Project TA",
                "CS300",
                "Support project coordination",
                new String[]{"java", "Communication", "Communication"},
                "Mentoring, Java, Mentoring",
                "5",
                "mo-2"
        );

        assertTrue(successResult.isSuccess());
        assertEquals(
                List.of("Java", "Communication", "Mentoring"),
                service.listAllOpenJobs().get(0).getRequiredSkills()
        );

        JobCreateResult invalidResult = service.createJob(
                "Broken Job",
                "CS999",
                "Invalid weekly hours",
                new String[]{"Java"},
                "",
                "abc",
                "mo-2"
        );

        assertFalse(invalidResult.isSuccess());
        assertTrue(invalidResult.getErrors().contains("Weekly hours must be a positive integer."));
    }

    @Test
    void listJobsByMoUserShouldKeepOwnersJobsIncludingClosedOnes() {
        JobService service = new JobService(
                new JobRepository(tempDir.resolve("jobs.json")),
                new ProfileRepository(tempDir.resolve("profiles.json"))
        );

        assertTrue(service.createJob(
                "Open Job",
                "CS111",
                "Still hiring",
                new String[]{"Java"},
                "",
                "6",
                "mo-1"
        ).isSuccess());
        assertTrue(service.createJob(
                "Other Owner Job",
                "CS222",
                "Different owner",
                new String[]{"Python"},
                "",
                "5",
                "mo-2"
        ).isSuccess());
        assertTrue(service.createJob(
                "Soon Closed Job",
                "CS333",
                "Will be closed",
                new String[]{"Communication"},
                "",
                "4",
                "mo-1"
        ).isSuccess());

        String closedJobId = service.listJobsByMoUser("mo-1").stream()
                .filter(job -> "Soon Closed Job".equals(job.getTitle()))
                .findFirst()
                .orElseThrow()
                .getId();

        assertTrue(service.updateJobStatus(closedJobId, "mo-1", "CLOSED"));

        List<String> ownedTitles = service.listJobsByMoUser("mo-1").stream()
                .map(job -> job.getTitle() + ":" + job.getStatus())
                .toList();

        assertEquals(List.of("Open Job:OPEN", "Soon Closed Job:CLOSED"), ownedTitles);
        assertEquals(
                List.of("Open Job", "Other Owner Job"),
                service.listAllOpenJobs().stream().map(job -> job.getTitle()).sorted().toList()
        );
    }

    @Test
    void candidatePreviewShouldRankProfilesByPredefinedSkillCoverage() {
        JobRepository jobRepository = new JobRepository(tempDir.resolve("jobs.json"));
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles.json"));
        JobService service = new JobService(jobRepository, profileRepository);

        JobCreateResult result = service.createJob(
                "Algorithms Job",
                "CS444",
                "Need Java and algorithms support",
                new String[]{"Java", "Algorithms"},
                "",
                "8",
                "mo-1"
        );
        assertTrue(result.isSuccess());

        profileRepository.save(new Profile(
                "ta-1",
                "Alice",
                "20260001",
                "Computer Science",
                "alice@example.com",
                List.of("Java", "Algorithms"),
                List.of("Mentoring"),
                null
        ));
        profileRepository.save(new Profile(
                "ta-2",
                "Bob",
                "20260002",
                "Software Engineering",
                "bob@example.com",
                List.of("Java"),
                List.of(),
                null
        ));
        profileRepository.save(new Profile(
                "ta-3",
                "Carol",
                "20260003",
                "Mathematics",
                "carol@example.com",
                List.of("Presentation"),
                List.of(),
                null
        ));

        List<JobCandidateView> candidates = service.listCandidateMatches(result.getJob().getId(), "mo-1");

        assertEquals(3, candidates.size());
        assertEquals("Alice", candidates.get(0).getDisplayName());
        assertEquals(100, candidates.get(0).getMatchPercent());
        assertEquals("Bob", candidates.get(1).getDisplayName());
        assertEquals(50, candidates.get(1).getMatchPercent());
        assertEquals(List.of("Algorithms"), candidates.get(1).getMissingSkills());
        assertEquals("Carol", candidates.get(2).getDisplayName());
        assertEquals(0, candidates.get(2).getMatchPercent());
    }

    @Test
    void draftJobsShouldBeSavedWithoutFullValidationAndHiddenFromTaListings() {
        JobService service = new JobService(
                new JobRepository(tempDir.resolve("jobs.json")),
                new ProfileRepository(tempDir.resolve("profiles.json"))
        );

        JobCreateResult draftResult = service.createDraft(
                "",
                "",
                "",
                null,
                "",
                "",
                "mo-1"
        );

        assertTrue(draftResult.isSuccess());
        assertTrue(draftResult.getJob().isDraft());
        assertTrue(service.listAllOpenJobs().isEmpty());
        assertEquals(
                List.of(Job.STATUS_DRAFT),
                service.listJobsByMoUser("mo-1").stream().map(Job::getStatus).toList()
        );
    }

    @Test
    void publishingDraftShouldRequireCompleteFieldsBeforeBecomingOpen() {
        JobService service = new JobService(
                new JobRepository(tempDir.resolve("jobs.json")),
                new ProfileRepository(tempDir.resolve("profiles.json"))
        );

        JobCreateResult draftResult = service.createDraft(
                "",
                "",
                "",
                null,
                "",
                "",
                "mo-1"
        );

        assertTrue(draftResult.isSuccess());

        JobCreateResult invalidPublishResult = service.publishJob(
                draftResult.getJob().getId(),
                "",
                "",
                "",
                null,
                "",
                "",
                "mo-1"
        );

        assertFalse(invalidPublishResult.isSuccess());
        assertTrue(invalidPublishResult.getErrors().contains("Title is required."));
        assertTrue(service.listAllOpenJobs().isEmpty());

        JobCreateResult publishResult = service.publishJob(
                draftResult.getJob().getId(),
                "Draft Ready Job",
                "CS555",
                "Now complete and ready to publish",
                new String[]{"Java", "Communication"},
                "",
                "6",
                "mo-1"
        );

        assertTrue(publishResult.isSuccess());
        assertTrue(publishResult.getJob().isOpen());
        assertEquals(
                List.of("Draft Ready Job"),
                service.listAllOpenJobs().stream().map(Job::getTitle).toList()
        );
    }

    @Test
    void taJobViewsShouldExposeModuleOwnerDisplayNameForOpenJobs() {
        JobRepository jobRepository = new JobRepository(tempDir.resolve("jobs.json"));
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles.json"));
        UserRepository userRepository = new UserRepository(tempDir.resolve("users.json"));
        JobService service = new JobService(jobRepository, profileRepository, userRepository);

        userRepository.save(new User("mo-1", "dr-smith", "", Role.MO, "smith@example.com"));

        assertTrue(service.createJob(
                "Programming TA",
                "CS101",
                "Support labs",
                new String[]{"Java", "Communication"},
                "",
                "8",
                "mo-1"
        ).isSuccess());
        assertTrue(service.createDraft(
                "Hidden Draft",
                "CS102",
                "Should not appear",
                new String[]{"Java"},
                "",
                "6",
                "mo-1"
        ).isSuccess());

        Profile profile = new Profile();
        profile.setSelectedSkills(List.of("Java", "Communication"));

        List<TaJobView> jobViews = service.listTaJobViews(profile);

        assertEquals(1, jobViews.size());
        assertEquals("Programming TA", jobViews.get(0).getJob().getTitle());
        assertEquals("dr-smith", jobViews.get(0).getModuleOwnerDisplayName());
        assertEquals("smith@example.com", jobViews.get(0).getModuleOwnerEmail());
        assertTrue(jobViews.get(0).isMatchedSkill("Java"));
        assertFalse(jobViews.get(0).isMissingSkill("Java"));
    }

    @Test
    void findTaJobViewShouldReturnOnlyOpenJobs() {
        JobRepository jobRepository = new JobRepository(tempDir.resolve("jobs.json"));
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles.json"));
        UserRepository userRepository = new UserRepository(tempDir.resolve("users.json"));
        JobService service = new JobService(jobRepository, profileRepository, userRepository);

        userRepository.save(new User("mo-1", "mo-demo", "", Role.MO, "mo@example.com"));

        JobCreateResult openJob = service.createJob(
                "Visible Job",
                "CS201",
                "Visible to TAs",
                new String[]{"Java"},
                "",
                "6",
                "mo-1"
        );
        JobCreateResult draftJob = service.createDraft(
                "Draft Job",
                "CS202",
                "Still private",
                new String[]{"Python"},
                "",
                "5",
                "mo-1"
        );

        Profile profile = new Profile();
        profile.setSelectedSkills(List.of("Java"));

        assertTrue(service.findTaJobView(openJob.getJob().getId(), profile).isPresent());
        assertTrue(service.findTaJobView(draftJob.getJob().getId(), profile).isEmpty());
    }

    @Test
    void taJobViewsShouldSupportKeywordFilteringAcrossJobFields() {
        JobRepository jobRepository = new JobRepository(tempDir.resolve("jobs-filter-keyword.json"));
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles-filter-keyword.json"));
        UserRepository userRepository = new UserRepository(tempDir.resolve("users-filter-keyword.json"));
        JobService service = new JobService(jobRepository, profileRepository, userRepository);

        userRepository.save(new User("mo-1", "dr-smith", "", Role.MO, "smith@example.com"));

        assertTrue(service.createJob(
                "Programming in Rust",
                "CS110L",
                "Support systems programming labs",
                new String[]{"Communication"},
                "",
                "6",
                "mo-1"
        ).isSuccess());
        assertTrue(service.createJob(
                "Machine Learning TA",
                "CS220",
                "Support ML practical sessions",
                new String[]{"Machine Learning", "Python"},
                "",
                "8",
                "mo-1"
        ).isSuccess());

        Profile profile = new Profile();
        profile.setSelectedSkills(List.of("Communication"));

        List<TaJobView> moduleCodeMatches = service.listTaJobViews(profile, "CS110", List.of());
        List<TaJobView> ownerMatches = service.listTaJobViews(profile, "smith@example.com", List.of());
        List<TaJobView> skillMatches = service.listTaJobViews(profile, "machine learning", List.of());

        assertEquals(List.of("Programming in Rust"), moduleCodeMatches.stream().map(view -> view.getJob().getTitle()).toList());
        assertEquals(2, ownerMatches.size());
        assertEquals(List.of("Machine Learning TA"), skillMatches.stream().map(view -> view.getJob().getTitle()).toList());
    }

    @Test
    void taJobViewsShouldSupportMultiSkillFiltering() {
        JobService service = new JobService(
                new JobRepository(tempDir.resolve("jobs-filter-skills.json")),
                new ProfileRepository(tempDir.resolve("profiles-filter-skills.json"))
        );

        assertTrue(service.createJob(
                "Programming TA",
                "CS101",
                "Support labs",
                new String[]{"Java", "Communication"},
                "",
                "8",
                "mo-1"
        ).isSuccess());
        assertTrue(service.createJob(
                "Algorithms TA",
                "CS202",
                "Guide algorithm tutorials",
                new String[]{"Java", "Algorithms"},
                "",
                "6",
                "mo-1"
        ).isSuccess());
        assertTrue(service.createJob(
                "Presentation TA",
                "CS303",
                "Coach presentations",
                new String[]{"Presentation"},
                "",
                "4",
                "mo-1"
        ).isSuccess());

        Profile profile = new Profile();
        profile.setSelectedSkills(List.of("Java", "Communication", "Algorithms"));

        List<TaJobView> javaOnlyMatches = service.listTaJobViews(profile, "", List.of("Java"));
        List<TaJobView> javaCommunicationMatches = service.listTaJobViews(profile, "", List.of("Java", "Communication"));
        List<TaJobView> communicationPythonMatches = service.listTaJobViews(profile, "", List.of("Communication", "Python"));

        assertEquals(List.of("Algorithms TA", "Programming TA"), javaOnlyMatches.stream().map(view -> view.getJob().getTitle()).toList());
        assertEquals(List.of("Programming TA"), javaCommunicationMatches.stream().map(view -> view.getJob().getTitle()).toList());
        assertTrue(communicationPythonMatches.isEmpty());
    }
}
