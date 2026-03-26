package com.group72.tarecruitment.repository.json;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.util.JsonFileStore;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobRepository extends JsonRepository<Job> {
    public JobRepository() {
        this(new JsonFileStore(), AppConfig.resolveDataFile("jobs.json"));
    }

    public JobRepository(Path filePath) {
        this(new JsonFileStore(), filePath);
    }

    protected JobRepository(JsonFileStore fileStore, Path filePath) {
        super(fileStore, filePath, Job.class);
    }

    public List<Job> findAll() {
        return new ArrayList<>(findAllInternal());
    }

    public Optional<Job> findById(String jobId) {
        return findAllInternal().stream()
                .filter(job -> job.getId() != null && job.getId().equals(jobId))
                .findFirst();
    }

    public void save(Job job) {
        List<Job> jobs = findAllInternal();
        jobs.removeIf(item -> item.getId() != null && item.getId().equals(job.getId()));
        jobs.add(job);
        saveAllInternal(jobs);
    }
}
