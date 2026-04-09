package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.model.Job;
import com.group72.tarecruitment.model.MoApplicationFilterCriteria;
import com.group72.tarecruitment.model.MoApplicationView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.util.SkillCatalog;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        MoApplicationFilterCriteria criteria = new MoApplicationFilterCriteria(
                request.getParameter("keyword"),
                request.getParameter("major"),
                request.getParameter("status"),
                SkillCatalog.normalizeSelectedSkills(
                        request.getParameterValues("filterSkills") == null ? List.of() : List.of(request.getParameterValues("filterSkills"))
                )
        );
        List<Job> jobs = jobService.listJobsByMoUser(currentUser.getId());
        List<MoApplicationView> applications = jobId == null || jobId.isBlank()
                ? applicationService.listMoApplicationViews(currentUser.getId(), criteria)
                : applicationService.listMoApplicationViewsForJob(jobId, currentUser.getId(), criteria);

        request.setAttribute("applications", applications);
        request.setAttribute("jobs", jobs);
        request.setAttribute("availableMajors", applicationService.listMoApplicationMajors(currentUser.getId()));
        request.setAttribute("availableSkills", applicationService.listMoApplicationSkills(currentUser.getId()));
        request.setAttribute("statusOptions", List.of("PENDING", "SHORTLISTED", "REJECTED", "WITHDRAWN"));
        request.setAttribute("filterKeyword", criteria.getKeyword());
        request.setAttribute("filterMajor", criteria.getMajor());
        request.setAttribute("filterStatus", criteria.getStatus());
        request.setAttribute("selectedFilterSkills", criteria.getSelectedSkills());
        request.setAttribute("selectedFilterSkillLookup", buildSelectedSkillLookup(criteria.getSelectedSkills()));
        request.setAttribute("hasActiveFilters", criteria.hasActiveFilters());
        request.setAttribute("selectedJobId", jobId == null ? "" : jobId);
        request.setAttribute("selectedJobTitle", resolveSelectedJobTitle(jobs, jobId));
        request.setAttribute("applicationSummary", buildStatusSummary(applications));
        request.setAttribute("activeFilterChips", buildActiveFilterChips(criteria));
        request.setAttribute("clearFilterHref", request.getContextPath() + "/mo/applications"
                + ((jobId == null || jobId.isBlank()) ? "" : "?jobId=" + jobId));
        request.getRequestDispatcher(ViewPaths.MO_APPLICATIONS).forward(request, response);
    }

    private Map<String, Boolean> buildSelectedSkillLookup(List<String> selectedFilterSkills) {
        Map<String, Boolean> lookup = new LinkedHashMap<>();
        for (String skill : selectedFilterSkills) {
            lookup.put(skill, Boolean.TRUE);
        }
        return lookup;
    }

    private String resolveSelectedJobTitle(List<Job> jobs, String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return "";
        }

        for (Job job : jobs) {
            if (job != null && jobId.equals(job.getId())) {
                if (job.getTitle() != null && !job.getTitle().isBlank()) {
                    return job.getTitle();
                }
                return job.getModuleCode() == null ? "" : job.getModuleCode();
            }
        }
        return "";
    }

    private Map<String, Integer> buildStatusSummary(List<MoApplicationView> applications) {
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("Visible", applications.size());
        summary.put("Pending", 0);
        summary.put("Shortlisted", 0);
        summary.put("Rejected", 0);
        summary.put("Withdrawn", 0);

        for (MoApplicationView applicationView : applications) {
            Application application = applicationView.getApplication();
            if (application == null) {
                continue;
            }
            if (application.isPending()) {
                summary.put("Pending", summary.get("Pending") + 1);
            } else if (application.isShortlisted()) {
                summary.put("Shortlisted", summary.get("Shortlisted") + 1);
            } else if (application.isRejected()) {
                summary.put("Rejected", summary.get("Rejected") + 1);
            } else if (application.isWithdrawn()) {
                summary.put("Withdrawn", summary.get("Withdrawn") + 1);
            }
        }

        return summary;
    }

    private List<String> buildActiveFilterChips(MoApplicationFilterCriteria criteria) {
        List<String> chips = new ArrayList<>();
        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            chips.add("Keyword: " + criteria.getKeyword().trim());
        }
        if (criteria.getMajor() != null && !criteria.getMajor().isBlank()) {
            chips.add("Major: " + criteria.getMajor().trim());
        }
        if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
            chips.add("Status: " + criteria.getStatus().trim());
        }
        for (String skill : criteria.getSelectedSkills()) {
            chips.add("Skill: " + skill);
        }
        return chips;
    }
}
