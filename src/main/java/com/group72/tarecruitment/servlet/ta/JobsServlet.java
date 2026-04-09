package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.TaJobView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.JobService;
import com.group72.tarecruitment.service.ProfileService;
import com.group72.tarecruitment.util.SkillCatalog;
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

@WebServlet("/ta/jobs")
public class JobsServlet extends HttpServlet {
    private static final int PAGE_SIZE = 5;

    private transient JobService jobService;
    private transient ProfileService profileService;

    @Override
    public void init() {
        this.jobService = new JobService(new JobRepository(), new ProfileRepository(), new UserRepository());
        this.profileService = new ProfileService(new ProfileRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        Profile profile = profileService.getOrCreateProfile(currentUser);
        String keyword = request.getParameter("keyword");
        List<String> selectedFilterSkills = SkillCatalog.normalizeSelectedSkills(
                request.getParameterValues("filterSkills") == null ? List.of() : List.of(request.getParameterValues("filterSkills"))
        );
        List<TaJobView> allJobMatches = jobService.listTaJobViews(profile, keyword, selectedFilterSkills);
        int currentPage = parsePage(request.getParameter("page"));
        int totalPages = Math.max(1, (int) Math.ceil(allJobMatches.size() / (double) PAGE_SIZE));
        int safePage = Math.min(currentPage, totalPages);
        int fromIndex = (safePage - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allJobMatches.size());
        List<TaJobView> pageItems = allJobMatches.isEmpty() ? List.of() : allJobMatches.subList(fromIndex, toIndex);

        request.setAttribute("profile", profile);
        request.setAttribute("availableSkills", jobService.getAvailableSkills());
        request.setAttribute("keyword", keyword == null ? "" : keyword.trim());
        request.setAttribute("selectedFilterSkills", selectedFilterSkills);
        request.setAttribute("selectedFilterSkillLookup", buildSelectedSkillLookup(selectedFilterSkills));
        request.setAttribute("hasActiveFilters", (keyword != null && !keyword.isBlank()) || !selectedFilterSkills.isEmpty());
        request.setAttribute("jobMatches", pageItems);
        request.setAttribute("currentPage", safePage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("hasPreviousPage", safePage > 1);
        request.setAttribute("hasNextPage", safePage < totalPages);
        request.setAttribute("previousPage", safePage - 1);
        request.setAttribute("nextPage", safePage + 1);
        request.getRequestDispatcher(ViewPaths.TA_JOBS).forward(request, response);
    }

    private int parsePage(String pageInput) {
        if (pageInput == null || pageInput.isBlank()) {
            return 1;
        }

        try {
            return Math.max(1, Integer.parseInt(pageInput.trim()));
        } catch (NumberFormatException exception) {
            return 1;
        }
    }

    private Map<String, Boolean> buildSelectedSkillLookup(List<String> selectedFilterSkills) {
        Map<String, Boolean> lookup = new LinkedHashMap<>();
        for (String skill : selectedFilterSkills) {
            lookup.put(skill, Boolean.TRUE);
        }
        return lookup;
    }
}
