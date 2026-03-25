package com.group72.tarecruitment.servlet.auth;

import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.AuthService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private transient AuthService authService;

    @Override
    public void init() {
        this.authService = new AuthService(new UserRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(ViewPaths.LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Optional<User> authenticatedUser = authService.authenticate(username, password);
        if (authenticatedUser.isEmpty()) {
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher(ViewPaths.LOGIN).forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", authenticatedUser.get());
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
