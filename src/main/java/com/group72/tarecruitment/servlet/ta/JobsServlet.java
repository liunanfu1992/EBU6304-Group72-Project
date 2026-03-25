package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.service.ProfileService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ta/jobs")
public class JobsServlet extends HttpServlet {
    private transient JobService jobService;
    private transient ProfileService profileService;

    @Override
    public void init() {
        this.jobService = new JobService(new JobRepository());
        this.profileService = new ProfileService(new ProfileRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        Profile profile = profileService.getOrCreateProfile(currentUser);

        request.setAttribute("profile", profile);
        request.setAttribute("jobMatches", jobService.listMatchesForProfile(profile));
        request.getRequestDispatcher(ViewPaths.TA_JOBS).forward(request, response);
    }
}
