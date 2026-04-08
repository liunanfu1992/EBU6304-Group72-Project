package com.group72.tarecruitment.repository.json;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.util.JsonFileStore;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationRepository extends JsonRepository<Application> {
    public ApplicationRepository() {
        this(new JsonFileStore(true), AppConfig.resolveDataFile("applications.json"));
    }

    public ApplicationRepository(Path filePath) {
        this(new JsonFileStore(true), filePath);
    }

    protected ApplicationRepository(JsonFileStore fileStore, Path filePath) {
        super(fileStore, filePath, Application.class);
    }

    public List<Application> findAll() {
        return new ArrayList<>(findAllInternal());
    }

    public Optional<Application> findById(String applicationId) {
        return findAllInternal().stream()
                .filter(application -> application.getId() != null && application.getId().equals(applicationId))
                .findFirst();
    }

    public List<Application> findByTaUserId(String taUserId) {
        return findAllInternal().stream()
                .filter(application -> taUserId != null && taUserId.equals(application.getTaUserId()))
                .toList();
    }

    public List<Application> findByJobId(String jobId) {
        return findAllInternal().stream()
                .filter(application -> jobId != null && jobId.equals(application.getJobId()))
                .toList();
    }

    public Optional<Application> findByTaUserIdAndJobId(String taUserId, String jobId) {
        return findAllInternal().stream()
                .filter(application -> taUserId != null && taUserId.equals(application.getTaUserId()))
                .filter(application -> jobId != null && jobId.equals(application.getJobId()))
                .findFirst();
    }

    public void save(Application application) {
        List<Application> applications = findAllInternal();
        applications.removeIf(item -> item.getId() != null && item.getId().equals(application.getId()));
        applications.add(application);
        saveAllInternal(applications);
    }
}
