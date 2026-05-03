package com.group72.tarecruitment.servlet.auth;

import com.group72.tarecruitment.model.RegistrationResult;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.AuthService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private transient AuthService authService;

    @Override
    public void init() {
        this.authService = new AuthService(new UserRepository(), new ProfileRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("roleOptions", new String[]{Role.TA.name(), Role.MO.name()});
        request.getRequestDispatcher(ViewPaths.REGISTER).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Role role = parseRole(request.getParameter("role"));
        RegistrationResult result = authService.registerAccount(
                request.getParameter("username"),
                request.getParameter("password"),
                role,
                request.getParameter("email")
        );

        if (!result.isSuccess()) {
            request.setAttribute("errors", result.getErrors());
            request.setAttribute("formUsername", request.getParameter("username"));
            request.setAttribute("formEmail", request.getParameter("email"));
            request.setAttribute("selectedRole", role == null ? "" : role.name());
            request.setAttribute("roleOptions", new String[]{Role.TA.name(), Role.MO.name()});
            request.getRequestDispatcher(ViewPaths.REGISTER).forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login?registered=1");
    }

    private Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
