package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mo/jobs/new")
public class JobCreateServlet extends HttpServlet {
    private transient JobService jobService;

    @Override
    public void init() {
        this.jobService = new JobService(new JobRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(ViewPaths.MO_JOB_FORM).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        jobService.createJob(
                request.getParameter("title"),
                request.getParameter("moduleCode"),
                request.getParameter("description"),
                request.getParameter("requiredSkills"),
                request.getParameter("weeklyHours"),
                currentUser.getId()
        );

        response.sendRedirect(request.getContextPath() + "/mo/jobs/new?created=1");
    }
}
