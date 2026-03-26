package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.service.JobService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mo/jobs/status")
public class MoJobStatusServlet extends HttpServlet {
    private transient JobService jobService;

    @Override
    public void init() {
        this.jobService = new JobService(new JobRepository());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String jobId = request.getParameter("jobId");
        String status = request.getParameter("status");
        String source = request.getParameter("source");

        boolean updated = jobService.updateJobStatus(jobId, currentUser.getId(), status);
        if ("detail".equalsIgnoreCase(source)) {
            response.sendRedirect(request.getContextPath() + "/mo/jobs/view?jobId=" + jobId
                    + (updated ? "&statusUpdated=1" : "&notFound=1"));
            return;
        }

        response.sendRedirect(request.getContextPath() + "/mo/jobs" + (updated ? "?statusUpdated=1" : "?notFound=1"));
    }
}
