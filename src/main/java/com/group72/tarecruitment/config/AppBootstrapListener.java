package com.group72.tarecruitment.config;

import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.AuthService;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.repository.json.JobRepository;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppBootstrapListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AppConfig.initialize(sce.getServletContext());
        AuthService authService = new AuthService(new UserRepository());
        authService.ensureDefaultUsers();

        JobService jobService = new JobService(new JobRepository());
        jobService.ensureSampleJobs();
    }
}
