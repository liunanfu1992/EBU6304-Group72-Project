package com.group72.tarecruitment.service;

import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.JobCandidateView;
import com.group72.tarecruitment.model.JobCreateResult;
import com.group72.tarecruitment.model.JobMatchView;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.util.FormUtils;
import com.group72.tarecruitment.util.SkillCatalog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JobService {
    private final JobRepository jobRepository;
    private final ProfileRepository profileRepository;

    public JobService(JobRepository jobRepository) {
        this(jobRepository, new ProfileRepository());
    }

    public JobService(JobRepository jobRepository, ProfileRepository profileRepository) {
        this.jobRepository = jobRepository;
        this.profileRepository = profileRepository;
    }

    public List<Job> listAllOpenJobs() {
        return jobRepository.findAll().stream()
                .filter(job -> job.getStatus() == null || !"CLOSED".equalsIgnoreCase(job.getStatus()))
                .collect(Collectors.toList());
    }

    public List<JobMatchView> listMatchesForProfile(Profile profile) {
        List<String> profileSkills = profile == null
                ? List.of()
                : SkillCatalog.extractPredefinedSkills(profile.getSelectedSkills());

        return listAllOpenJobs().stream()
                .map(job -> toMatchView(job, profileSkills))
                .sorted(Comparator
                        .comparingInt(JobMatchView::getMatchPercent).reversed()
                        .thenComparing(match -> match.getJob().getTitle(), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<String> getAvailableSkills() {
        return SkillCatalog.PREDEFINED_SKILLS;
    }

    public List<Job> listJobsByMoUser(String moUserId) {
        return jobRepository.findAll().stream()
                .filter(job -> moUserId != null && moUserId.equals(job.getMoUserId()))
                .sorted(Comparator
                        .comparing(Job::isClosed)
                        .thenComparing(Job::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<Job> findOwnedJob(String jobId, String moUserId) {
        return jobRepository.findById(jobId)
                .filter(job -> moUserId != null && moUserId.equals(job.getMoUserId()));
    }

    public JobCreateResult updateJob(String jobId, String title, String moduleCode, String description,
                                     String[] selectedSkills, String customSkillsInput, String weeklyHours,
                                     String moUserId) {
        Optional<Job> existingJob = findOwnedJob(jobId, moUserId);
        if (existingJob.isEmpty()) {
            return new JobCreateResult(false, buildDraftJob(jobId, title, moduleCode, description, selectedSkills,
                    customSkillsInput, weeklyHours, moUserId, "OPEN"), List.of("Job not found."));
        }

        Job job = existingJob.get();
        Job updatedJob = buildDraftJob(job.getId(), title, moduleCode, description, selectedSkills, customSkillsInput,
                weeklyHours, moUserId, job.getStatus());
        return validateAndSaveJob(updatedJob, weeklyHours);
    }

    public boolean updateJobStatus(String jobId, String moUserId, String status) {
        Optional<Job> existingJob = findOwnedJob(jobId, moUserId);
        if (existingJob.isEmpty()) {
            return false;
        }

        String normalizedStatus = normalizeStatus(status);
        if (normalizedStatus == null) {
            return false;
        }

        Job job = existingJob.get();
        job.setStatus(normalizedStatus);
        jobRepository.save(job);
        return true;
    }

    public List<JobCandidateView> listCandidateMatches(String jobId, String moUserId) {
        Optional<Job> job = findOwnedJob(jobId, moUserId);
        if (job.isEmpty()) {
            return List.of();
        }

        return profileRepository.findAll().stream()
                .filter(this::isCandidateProfileReady)
                .map(profile -> toCandidateView(job.get(), profile))
                .sorted(Comparator
                        .comparingInt(JobCandidateView::getMatchPercent).reversed()
                        .thenComparing(JobCandidateView::getDisplayName, String.CASE_INSENSITIVE_ORDER))
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

    public JobCreateResult createJob(String title, String moduleCode, String description, String[] selectedSkills,
                                     String customSkillsInput, String weeklyHours, String moUserId) {
        Job job = buildDraftJob(null, title, moduleCode, description, selectedSkills, customSkillsInput,
                weeklyHours, moUserId, "OPEN");
        return validateAndSaveJob(job, weeklyHours);
    }

    private JobCreateResult validateAndSaveJob(Job job, String weeklyHoursInput) {
        List<String> errors = validateJob(job, weeklyHoursInput);
        if (!errors.isEmpty()) {
            return new JobCreateResult(false, job, errors);
        }

        jobRepository.save(job);
        return new JobCreateResult(true, job, List.of());
    }

    private Job buildDraftJob(String jobId, String title, String moduleCode, String description,
                              String[] selectedSkills, String customSkillsInput, String weeklyHours,
                              String moUserId, String status) {
        List<String> normalizedSelectedSkills = SkillCatalog.normalizeSelectedSkills(
                selectedSkills == null ? List.of() : List.of(selectedSkills)
        );
        List<String> normalizedCustomSkills = SkillCatalog.normalizeCustomSkills(
                FormUtils.parseTags(customSkillsInput),
                normalizedSelectedSkills
        );

        return new Job(
                jobId == null || jobId.isBlank() ? UUID.randomUUID().toString() : jobId,
                safeTrim(title),
                safeTrim(moduleCode),
                safeTrim(description),
                SkillCatalog.mergeSkills(normalizedSelectedSkills, normalizedCustomSkills),
                parseWeeklyHours(weeklyHours),
                moUserId,
                normalizeStatus(status) == null ? "OPEN" : normalizeStatus(status)
        );
    }

    private JobMatchView toMatchView(Job job, List<String> profileSkills) {
        List<String> requiredSkills = SkillCatalog.extractPredefinedSkills(job.getRequiredSkills());
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String requiredSkill : requiredSkills) {
            if (profileSkills.contains(requiredSkill)) {
                matchedSkills.add(requiredSkill);
            } else {
                missingSkills.add(requiredSkill);
            }
        }

        int matchPercent = requiredSkills.isEmpty() ? 100 : matchedSkills.size() * 100 / requiredSkills.size();
        return new JobMatchView(job, matchedSkills, missingSkills, matchPercent);
    }

    private JobCandidateView toCandidateView(Job job, Profile profile) {
        List<String> requiredSkills = SkillCatalog.extractPredefinedSkills(job.getRequiredSkills());
        List<String> profileSkills = SkillCatalog.extractPredefinedSkills(profile.getSelectedSkills());
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String requiredSkill : requiredSkills) {
            if (profileSkills.contains(requiredSkill)) {
                matchedSkills.add(requiredSkill);
            } else {
                missingSkills.add(requiredSkill);
            }
        }

        int matchPercent = requiredSkills.isEmpty() ? 100 : matchedSkills.size() * 100 / requiredSkills.size();
        return new JobCandidateView(profile, matchedSkills, missingSkills, matchPercent);
    }

    private boolean isCandidateProfileReady(Profile profile) {
        return profile != null
                && profile.hasSelectedSkills()
                && !isBlank(profile.getName())
                && !isBlank(profile.getEmail());
    }

    private Integer parseWeeklyHours(String weeklyHours) {
        if (weeklyHours == null || weeklyHours.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(weeklyHours.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private List<String> validateJob(Job job, String weeklyHoursInput) {
        List<String> errors = new ArrayList<>();

        if (job.getTitle() == null || job.getTitle().isBlank()) {
            errors.add("Title is required.");
        }
        if (job.getModuleCode() == null || job.getModuleCode().isBlank()) {
            errors.add("Module code is required.");
        }
        if (job.getDescription() == null || job.getDescription().isBlank()) {
            errors.add("Description is required.");
        }
        if (weeklyHoursInput == null || weeklyHoursInput.isBlank()) {
            errors.add("Weekly hours is required.");
        } else if (job.getWeeklyHours() == null || job.getWeeklyHours() <= 0) {
            errors.add("Weekly hours must be a positive integer.");
        }

        return errors;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        if ("OPEN".equalsIgnoreCase(status.trim())) {
            return "OPEN";
        }
        if ("CLOSED".equalsIgnoreCase(status.trim())) {
            return "CLOSED";
        }
        return null;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
