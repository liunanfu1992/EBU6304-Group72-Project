package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.JobCreateResult;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mo/jobs/edit")
public class MoJobEditServlet extends HttpServlet {
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

        populateFormAttributes(request, job.get());
        request.getRequestDispatcher(ViewPaths.MO_JOB_FORM).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String jobId = request.getParameter("jobId");

        JobCreateResult result = jobService.updateJob(
                jobId,
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
            populateFormAttributes(request, result.getJob());
            request.getRequestDispatcher(ViewPaths.MO_JOB_FORM).forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/mo/jobs/view?jobId=" + result.getJob().getId() + "&updated=1");
    }

    private void populateFormAttributes(HttpServletRequest request, Job job) {
        request.setAttribute("jobDraft", job);
        request.setAttribute("availableSkills", jobService.getAvailableSkills());
        request.setAttribute("selectedSkillMap", toSkillSelectionMap(job.getPredefinedRequiredSkills()));
        request.setAttribute("pageTitle", "Edit Job Listing");
        request.setAttribute("pageDescription", "Update the job details and shared skill requirements for this posting.");
        request.setAttribute("formAction", request.getContextPath() + "/mo/jobs/edit");
        request.setAttribute("submitLabel", "Save Changes");
        request.setAttribute("cancelPath", request.getContextPath() + "/mo/jobs/view?jobId=" + job.getId());
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
