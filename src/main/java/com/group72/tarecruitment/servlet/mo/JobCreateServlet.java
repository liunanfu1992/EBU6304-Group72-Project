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
        populateFormAttributes(request);
        request.getRequestDispatcher(ViewPaths.MO_JOB_FORM).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String submitAction = request.getParameter("submitAction");
        boolean saveDraft = "saveDraft".equalsIgnoreCase(submitAction);
        JobCreateResult result = saveDraft
                ? jobService.createDraft(
                        request.getParameter("title"),
                        request.getParameter("moduleCode"),
                        request.getParameter("description"),
                        request.getParameterValues("selectedSkills"),
                        request.getParameter("customSkills"),
                        request.getParameter("weeklyHours"),
                        currentUser.getId()
                )
                : jobService.createJob(
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
            populateFormAttributes(request);
            request.setAttribute("selectedSkillMap", toSkillSelectionMap(result.getJob().getPredefinedRequiredSkills()));
            request.getRequestDispatcher(ViewPaths.MO_JOB_FORM).forward(request, response);
            return;
        }

        if (saveDraft) {
            response.sendRedirect(request.getContextPath() + "/mo/jobs/edit?jobId=" + result.getJob().getId() + "&draftSaved=1");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/mo/jobs/new?created=1");
    }

    private void populateFormAttributes(HttpServletRequest request) {
        request.setAttribute("availableSkills", jobService.getAvailableSkills());
        request.setAttribute("selectedSkillMap", Map.of());
        request.setAttribute("pageTitle", "Create Job Listing");
        request.setAttribute("pageDescription", "Publish a complete job now, or save a draft and finish it later without exposing it to TA users.");
        request.setAttribute("formAction", request.getContextPath() + "/mo/jobs/new");
        request.setAttribute("primarySubmitLabel", "Publish Job");
        request.setAttribute("primaryActionValue", "publish");
        request.setAttribute("cancelPath", request.getContextPath() + "/mo/dashboard");
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
