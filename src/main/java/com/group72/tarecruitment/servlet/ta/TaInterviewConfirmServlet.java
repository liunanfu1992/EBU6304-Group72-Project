package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.ApplicationActionResult;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ta/interviews/confirm")
public class TaInterviewConfirmServlet extends HttpServlet {
    private transient ApplicationService applicationService;

    @Override
    public void init() {
        this.applicationService = new ApplicationService(
                new ApplicationRepository(),
                new JobRepository(),
                new UserRepository()
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String applicationId = request.getParameter("applicationId");

        ApplicationActionResult result = applicationService.confirmInterviewAttendance(applicationId, currentUser.getId());
        if (result.isSuccess()) {
            response.sendRedirect(request.getContextPath() + "/ta/interviews?confirmed=1");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/ta/interviews?confirmError="
                + encode(result.getErrors().isEmpty() ? "1" : result.getErrors().get(0)));
    }

    private String encode(String value) {
        return value == null ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
