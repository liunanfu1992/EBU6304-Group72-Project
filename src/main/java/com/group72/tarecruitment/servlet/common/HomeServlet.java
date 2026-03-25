package com.group72.tarecruitment.servlet.common;

import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"", "/", "/home"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getRole() == Role.TA) {
            response.sendRedirect(request.getContextPath() + "/ta/dashboard");
            return;
        }
        if (currentUser.getRole() == Role.MO) {
            response.sendRedirect(request.getContextPath() + "/mo/dashboard");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
