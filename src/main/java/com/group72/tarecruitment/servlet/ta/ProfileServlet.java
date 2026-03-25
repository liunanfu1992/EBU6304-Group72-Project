package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.ProfileUpdateResult;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.service.ProfileService;
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

@WebServlet("/ta/profile")
public class ProfileServlet extends HttpServlet {
    private transient ProfileService profileService;

    @Override
    public void init() {
        this.profileService = new ProfileService(new ProfileRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        request.setAttribute("profile", profileService.getOrCreateProfile(currentUser));
        request.setAttribute("availableSkills", profileService.getAvailableSkills());
        request.setAttribute("selectedSkillMap", toSkillSelectionMap(profileService.getOrCreateProfile(currentUser).getSelectedSkills()));
        request.getRequestDispatcher(ViewPaths.TA_PROFILE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        ProfileUpdateResult result = profileService.updateProfile(
                currentUser,
                request.getParameter("name"),
                request.getParameter("studentId"),
                request.getParameter("major"),
                request.getParameter("email"),
                request.getParameterValues("selectedSkills"),
                request.getParameter("customSkills")
        );

        if (!result.isSuccess()) {
            request.setAttribute("profile", result.getProfile());
            request.setAttribute("errors", result.getErrors());
            request.setAttribute("availableSkills", profileService.getAvailableSkills());
            request.setAttribute("selectedSkillMap", toSkillSelectionMap(result.getProfile().getSelectedSkills()));
            request.getRequestDispatcher(ViewPaths.TA_PROFILE).forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/ta/profile?saved=1");
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
