package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mo/applications")
public class MoApplicationsServlet extends HttpServlet {
    private transient ApplicationService applicationService;
    private transient JobService jobService;

    @Override
    public void init() {
        ProfileRepository profileRepository = new ProfileRepository();
        UserRepository userRepository = new UserRepository();
        this.applicationService = new ApplicationService(
                new ApplicationRepository(),
                new JobRepository(),
                userRepository,
                profileRepository
        );
        this.jobService = new JobService(new JobRepository(), profileRepository, userRepository);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String jobId = request.getParameter("jobId");
        request.setAttribute("applications", jobId == null || jobId.isBlank()
                ? applicationService.listMoApplicationViews(currentUser.getId())
                : applicationService.listMoApplicationViewsForJob(jobId, currentUser.getId()));
        request.setAttribute("jobs", jobService.listJobsByMoUser(currentUser.getId()));
        request.setAttribute("selectedJobId", jobId == null ? "" : jobId);
        request.getRequestDispatcher(ViewPaths.MO_APPLICATIONS).forward(request, response);
    }
}
