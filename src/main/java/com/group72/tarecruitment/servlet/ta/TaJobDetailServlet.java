package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.TaJobView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.service.ProfileService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ta/jobs/view")
public class TaJobDetailServlet extends HttpServlet {
    private transient JobService jobService;
    private transient ProfileService profileService;
    private transient ApplicationService applicationService;

    @Override
    public void init() {
        this.jobService = new JobService(new JobRepository(), new ProfileRepository(), new UserRepository());
        this.profileService = new ProfileService(new ProfileRepository());
        this.applicationService = new ApplicationService(
                new ApplicationRepository(),
                new JobRepository(),
                new UserRepository()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        Profile profile = profileService.getOrCreateProfile(currentUser);
        String jobId = request.getParameter("jobId");

        Optional<TaJobView> jobView = jobService.findTaJobView(jobId, profile);
        if (jobView.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?notFound=1");
            return;
        }

        request.setAttribute("profile", profile);
        request.setAttribute("jobMatch", jobView.get());
        request.setAttribute("existingApplication", applicationService.findTaApplicationViewForJob(currentUser.getId(), jobId).orElse(null));
        request.getRequestDispatcher(ViewPaths.TA_JOB_DETAIL).forward(request, response);
    }
}
