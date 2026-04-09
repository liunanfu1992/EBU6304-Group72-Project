package com.group72.tarecruitment.service;

import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.model.ApplicationActionResult;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.MoApplicationFilterCriteria;
import com.group72.tarecruitment.model.MoApplicationView;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.TaApplicationView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository
    ) {
        this(applicationRepository, jobRepository, userRepository, null);
    }

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository,
            ProfileRepository profileRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    public List<Application> listApplicationsByTaUser(String taUserId) {
        return applicationRepository.findByTaUserId(taUserId).stream()
                .sorted(Comparator.comparing(Application::getCreatedAtEpochMillis, Comparator.nullsLast(Long::compareTo)).reversed())
                .toList();
    }

    public List<TaApplicationView> listTaApplicationViews(String taUserId) {
        return listApplicationsByTaUser(taUserId).stream()
                .map(this::toTaApplicationView)
                .toList();
    }

    public List<Application> listApplicationsForJob(String jobId, String moUserId) {
        Optional<Job> job = findOwnedJob(jobId, moUserId);
        if (job.isEmpty()) {
            return List.of();
        }

        return applicationRepository.findByJobId(jobId).stream()
                .sorted(Comparator.comparing(Application::getCreatedAtEpochMillis, Comparator.nullsLast(Long::compareTo)).reversed())
                .toList();
    }

    public List<Application> listApplicationsForMoUser(String moUserId) {
        return applicationRepository.findAll().stream()
                .filter(application -> isOwnedJob(application.getJobId(), moUserId))
                .sorted(Comparator.comparing(Application::getCreatedAtEpochMillis, Comparator.nullsLast(Long::compareTo)).reversed())
                .toList();
    }

    public List<MoApplicationView> listMoApplicationViews(String moUserId) {
        return listApplicationsForMoUser(moUserId).stream()
                .map(this::toMoApplicationView)
                .toList();
    }

    public List<MoApplicationView> listMoApplicationViews(String moUserId, MoApplicationFilterCriteria criteria) {
        MoApplicationFilterCriteria safeCriteria = criteria == null
                ? new MoApplicationFilterCriteria("", "", "", List.of())
                : criteria;

        return listMoApplicationViews(moUserId).stream()
                .filter(view -> matchesKeyword(view, safeCriteria.getKeyword()))
                .filter(view -> matchesMajor(view, safeCriteria.getMajor()))
                .filter(view -> matchesStatus(view, safeCriteria.getStatus()))
                .filter(view -> matchesSelectedSkills(view, safeCriteria.getSelectedSkills()))
                .toList();
    }

    public List<MoApplicationView> listMoApplicationViewsForJob(String jobId, String moUserId) {
        return listApplicationsForJob(jobId, moUserId).stream()
                .map(this::toMoApplicationView)
                .toList();
    }

    public List<MoApplicationView> listMoApplicationViewsForJob(String jobId, String moUserId, MoApplicationFilterCriteria criteria) {
        MoApplicationFilterCriteria safeCriteria = criteria == null
                ? new MoApplicationFilterCriteria("", "", "", List.of())
                : criteria;

        return listMoApplicationViewsForJob(jobId, moUserId).stream()
                .filter(view -> matchesKeyword(view, safeCriteria.getKeyword()))
                .filter(view -> matchesMajor(view, safeCriteria.getMajor()))
                .filter(view -> matchesStatus(view, safeCriteria.getStatus()))
                .filter(view -> matchesSelectedSkills(view, safeCriteria.getSelectedSkills()))
                .toList();
    }

    public Optional<Application> findTaApplication(String applicationId, String taUserId) {
        return applicationRepository.findById(applicationId)
                .filter(application -> taUserId != null && taUserId.equals(application.getTaUserId()));
    }

    public Optional<Application> findTaApplicationForJob(String taUserId, String jobId) {
        return applicationRepository.findByTaUserIdAndJobId(taUserId, jobId);
    }

    public Optional<TaApplicationView> findTaApplicationViewForJob(String taUserId, String jobId) {
        return findTaApplicationForJob(taUserId, jobId).map(this::toTaApplicationView);
    }

    public Optional<Application> findOwnedApplication(String applicationId, String moUserId) {
        return applicationRepository.findById(applicationId)
                .filter(application -> isOwnedJob(application.getJobId(), moUserId));
    }

    public Optional<MoApplicationView> findOwnedApplicationView(String applicationId, String moUserId) {
        return findOwnedApplication(applicationId, moUserId).map(this::toMoApplicationView);
    }

    public ApplicationActionResult applyToJob(String taUserId, String jobId) {
        Optional<User> taUser = userRepository.findById(taUserId)
                .filter(user -> user.getRole() == Role.TA);
        if (taUser.isEmpty()) {
            return new ApplicationActionResult(false, null, List.of("TA user not found."));
        }

        Optional<Job> job = jobRepository.findById(jobId).filter(Job::isOpen);
        if (job.isEmpty()) {
            return new ApplicationActionResult(false, null, List.of("Job is not available for application."));
        }

        Optional<Application> existingApplication = applicationRepository.findByTaUserIdAndJobId(taUserId, jobId);
        if (existingApplication.isPresent()) {
            return new ApplicationActionResult(false, existingApplication.get(), List.of("You have already applied for this job."));
        }

        long now = System.currentTimeMillis();
        Application application = new Application(
                UUID.randomUUID().toString(),
                taUserId,
                jobId,
                Application.STATUS_PENDING,
                now,
                now
        );
        applicationRepository.save(application);
        return new ApplicationActionResult(true, application, List.of());
    }

    public ApplicationActionResult withdrawApplication(String applicationId, String taUserId) {
        Optional<Application> application = findTaApplication(applicationId, taUserId);
        if (application.isEmpty()) {
            return new ApplicationActionResult(false, null, List.of("Application not found."));
        }

        if (!application.get().isPending()) {
            return new ApplicationActionResult(false, application.get(), List.of("Only pending applications can be withdrawn."));
        }

        application.get().setStatus(Application.STATUS_WITHDRAWN);
        application.get().setUpdatedAtEpochMillis(System.currentTimeMillis());
        applicationRepository.save(application.get());
        return new ApplicationActionResult(true, application.get(), List.of());
    }

    public ApplicationActionResult updateApplicationStatus(String applicationId, String moUserId, String status) {
        Optional<User> moUser = userRepository.findById(moUserId)
                .filter(user -> user.getRole() == Role.MO);
        if (moUser.isEmpty()) {
            return new ApplicationActionResult(false, null, List.of("MO user not found."));
        }

        Optional<Application> application = findOwnedApplication(applicationId, moUserId);
        if (application.isEmpty()) {
            return new ApplicationActionResult(false, null, List.of("Application not found."));
        }

        String normalizedStatus = normalizeReviewStatus(status);
        if (normalizedStatus == null) {
            return new ApplicationActionResult(false, application.get(), List.of("Invalid application status."));
        }
        if (application.get().isWithdrawn()) {
            return new ApplicationActionResult(false, application.get(), List.of("Withdrawn applications can no longer be processed."));
        }
        if (normalizedStatus.equalsIgnoreCase(application.get().getStatus())) {
            return new ApplicationActionResult(true, application.get(), List.of());
        }

        application.get().setStatus(normalizedStatus);
        application.get().setUpdatedAtEpochMillis(System.currentTimeMillis());
        applicationRepository.save(application.get());
        return new ApplicationActionResult(true, application.get(), List.of());
    }

    private Optional<Job> findOwnedJob(String jobId, String moUserId) {
        return jobRepository.findById(jobId)
                .filter(job -> moUserId != null && moUserId.equals(job.getMoUserId()));
    }

    private TaApplicationView toTaApplicationView(Application application) {
        Job job = jobRepository.findById(application.getJobId()).orElse(null);
        User moduleOwner = job == null ? null : userRepository.findById(job.getMoUserId()).orElse(null);
        String moduleOwnerDisplayName = moduleOwner == null
                ? "Module owner unavailable"
                : (moduleOwner.getUsername() == null || moduleOwner.getUsername().isBlank()
                        ? moduleOwner.getId()
                        : moduleOwner.getUsername());
        String moduleOwnerEmail = moduleOwner == null || moduleOwner.getEmail() == null ? "" : moduleOwner.getEmail();
        return new TaApplicationView(application, job, moduleOwnerDisplayName, moduleOwnerEmail);
    }

    private MoApplicationView toMoApplicationView(Application application) {
        Job job = jobRepository.findById(application.getJobId()).orElse(null);
        User taUser = userRepository.findById(application.getTaUserId()).orElse(null);
        Profile profile = profileRepository == null
                ? null
                : profileRepository.findByUserId(application.getTaUserId()).orElse(null);
        return new MoApplicationView(application, job, taUser, profile);
    }

    private boolean isOwnedJob(String jobId, String moUserId) {
        return findOwnedJob(jobId, moUserId).isPresent();
    }

    private boolean matchesKeyword(MoApplicationView view, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<String> fields = new ArrayList<>();
        fields.add(view.getCandidateDisplayName());
        fields.add(view.getCandidateEmail());
        fields.add(view.getStudentIdDisplay());
        fields.add(view.getMajorDisplay());
        fields.add(view.getStatusLabel());
        fields.add(view.getJob() == null ? "" : view.getJob().getTitle());
        fields.add(view.getJob() == null ? "" : view.getJob().getModuleCode());

        return fields.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(normalizedKeyword));
    }

    private boolean matchesMajor(MoApplicationView view, String major) {
        if (major == null || major.isBlank()) {
            return true;
        }
        return major.equalsIgnoreCase(view.getMajorDisplay());
    }

    private boolean matchesStatus(MoApplicationView view, String status) {
        if (status == null || status.isBlank()) {
            return true;
        }
        return status.equalsIgnoreCase(view.getStatusLabel());
    }

    private boolean matchesSelectedSkills(MoApplicationView view, List<String> selectedSkills) {
        if (selectedSkills == null || selectedSkills.isEmpty()) {
            return true;
        }
        List<String> candidateSkills = view.getPredefinedProfileSkills();
        return selectedSkills.stream().allMatch(candidateSkills::contains);
    }

    private String normalizeReviewStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        if (Application.STATUS_SHORTLISTED.equalsIgnoreCase(status.trim())) {
            return Application.STATUS_SHORTLISTED;
        }
        if (Application.STATUS_REJECTED.equalsIgnoreCase(status.trim())) {
            return Application.STATUS_REJECTED;
        }
        return null;
    }
}
