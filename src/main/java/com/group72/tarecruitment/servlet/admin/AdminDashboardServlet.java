package com.group72.tarecruitment.servlet.admin;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("appHome", AppConfig.getAppHome());
        request.getRequestDispatcher(ViewPaths.ADMIN_DASHBOARD).forward(request, response);
    }
}
