package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.service.JobService;
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

    @Override
    public void init() {
        this.jobService = new JobService(new JobRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("jobs", jobService.listAllOpenJobs());
        request.getRequestDispatcher(ViewPaths.TA_JOBS).forward(request, response);
    }
}
