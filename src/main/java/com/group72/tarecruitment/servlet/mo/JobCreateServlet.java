package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.JobCreateResult;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        request.setAttribute("availableSkills", jobService.getAvailableSkills());
        request.setAttribute("selectedSkillMap", Map.of());
        request.getRequestDispatcher(ViewPaths.MO_JOB_FORM).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        JobCreateResult result = jobService.createJob(
                request.getParameter("title"),
                request.getParameter("moduleCode"),
                request.getParameter("description"),
                request.getParameterValues("selectedSkills"),
                request.getParameter("customSkills"),
                request.getParameter("weeklyHours"),
                currentUser.getId()
        );

        if (!result.isSuccess()) {
            request.setAttribute("jobDraft", result.getJob());
            request.setAttribute("errors", result.getErrors());
            request.setAttribute("availableSkills", jobService.getAvailableSkills());
            request.setAttribute("selectedSkillMap", toSkillSelectionMap(result.getJob().getPredefinedRequiredSkills()));
            request.getRequestDispatcher(ViewPaths.MO_JOB_FORM).forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/mo/jobs/new?created=1");
    }

    private Map<String, Boolean> toSkillSelectionMap(List<String> skills) {
        Map<String, Boolean> selectionMap = new LinkedHashMap<>();
        if (skills == null) {
            return selectionMap;
        }

        for (String skill : skills) {
            selectionMap.put(skill, true);
        }
        return selectionMap;
    }
}
