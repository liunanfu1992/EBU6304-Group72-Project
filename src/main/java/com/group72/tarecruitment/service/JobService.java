package com.group72.tarecruitment.service;

import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.util.FormUtils;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> listAllOpenJobs() {
        return jobRepository.findAll().stream()
                .filter(job -> job.getStatus() == null || !"CLOSED".equalsIgnoreCase(job.getStatus()))
                .collect(Collectors.toList());
    }

    public void ensureSampleJobs() {
        if (!jobRepository.findAll().isEmpty()) {
            return;
        }

        List<Job> sampleJobs = List.of(
                new Job(
                        UUID.randomUUID().toString(),
                        "TA - Intro to Programming",
                        "CS101",
                        "Support lab sessions, answer student questions, and help with formative marking.",
                        List.of("Java", "Communication", "Marking"),
                        8,
                        "seed-mo",
                        "OPEN"
                ),
                new Job(
                        UUID.randomUUID().toString(),
                        "TA - Algorithms and Data Structures",
                        "CS202",
                        "Assist with tutorials, debug student code, and guide problem-solving sessions.",
                        List.of("Java", "Data Structures", "Algorithms"),
                        10,
                        "seed-mo",
                        "OPEN"
                ),
                new Job(
                        UUID.randomUUID().toString(),
                        "TA - Software Engineering Project",
                        "CS300",
                        "Support project reviews, team coordination, and milestone feedback activities.",
                        List.of("Communication", "Java", "Project Coordination"),
                        6,
                        "seed-mo",
                        "OPEN"
                )
        );

        sampleJobs.forEach(jobRepository::save);
    }

    public void createJob(String title, String moduleCode, String description, String skillsInput,
                          String weeklyHours, String moUserId) {
        Integer parsedHours = null;
        if (weeklyHours != null && !weeklyHours.isBlank()) {
            parsedHours = Integer.parseInt(weeklyHours.trim());
        }

        Job job = new Job(
                UUID.randomUUID().toString(),
                safeTrim(title),
                safeTrim(moduleCode),
                safeTrim(description),
                FormUtils.parseTags(skillsInput),
                parsedHours,
                moUserId,
                "OPEN"
        );

        jobRepository.save(job);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
