package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.service.ProfileService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
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
        request.setAttribute("profile", profileService.getOrCreateProfile(currentUser.getId()));
        request.getRequestDispatcher(ViewPaths.TA_PROFILE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        profileService.updateProfile(
                currentUser.getId(),
                request.getParameter("name"),
                request.getParameter("studentId"),
                request.getParameter("major"),
                request.getParameter("skills")
        );

        response.sendRedirect(request.getContextPath() + "/ta/profile?saved=1");
    }
}
