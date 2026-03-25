package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.group72.tarecruitment.model.JobCreateResult;
import com.group72.tarecruitment.model.JobMatchView;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.repository.json.JobRepository;
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
        JobService service = new JobService(new JobRepository(tempDir.resolve("jobs.json")));

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
        JobService service = new JobService(new JobRepository(tempDir.resolve("jobs.json")));

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
}
