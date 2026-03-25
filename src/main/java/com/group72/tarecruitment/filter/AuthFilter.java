package com.group72.tarecruitment.filter;

import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/ta/*", "/mo/*", "/admin/*"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String uri = httpRequest.getRequestURI();

        if (uri.contains("/ta/") && currentUser.getRole() != Role.TA) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
            return;
        }
        if (uri.contains("/mo/") && currentUser.getRole() != Role.MO) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
            return;
        }
        if (uri.contains("/admin/") && currentUser.getRole() != Role.ADMIN) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
            return;
        }

        chain.doFilter(request, response);
    }
}
