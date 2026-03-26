package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mo/jobs/view")
public class MoJobDetailServlet extends HttpServlet {
    private transient JobService jobService;

    @Override
    public void init() {
        this.jobService = new JobService(new JobRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String jobId = request.getParameter("jobId");

        Optional<Job> job = jobService.findOwnedJob(jobId, currentUser.getId());
        if (job.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/mo/jobs?notFound=1");
            return;
        }

        request.setAttribute("job", job.get());
        request.setAttribute("candidateMatches", jobService.listCandidateMatches(jobId, currentUser.getId()));
        request.getRequestDispatcher(ViewPaths.MO_JOB_DETAIL).forward(request, response);
    }
}
