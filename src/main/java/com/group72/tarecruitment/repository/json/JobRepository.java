package com.group72.tarecruitment.repository.json;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.util.JsonFileStore;
import java.util.ArrayList;
import java.util.List;

public class JobRepository extends JsonRepository<Job> {
    public JobRepository() {
        super(new JsonFileStore(), AppConfig.resolveDataFile("jobs.json"), Job.class);
    }

    public List<Job> findAll() {
        return new ArrayList<>(findAllInternal());
    }

    public void save(Job job) {
        List<Job> jobs = findAllInternal();
        jobs.removeIf(item -> item.getId() != null && item.getId().equals(job.getId()));
        jobs.add(job);
        saveAllInternal(jobs);
    }
}
