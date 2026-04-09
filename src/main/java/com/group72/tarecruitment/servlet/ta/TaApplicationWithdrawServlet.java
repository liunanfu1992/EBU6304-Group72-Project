package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.ApplicationActionResult;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ta/applications/withdraw")
public class TaApplicationWithdrawServlet extends HttpServlet {
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

        ApplicationActionResult result = applicationService.withdrawApplication(applicationId, currentUser.getId());
        if (result.isSuccess()) {
            response.sendRedirect(request.getContextPath() + "/ta/applications?withdrawn=1");
            return;
        }

        if (result.getErrors().contains("Only pending applications can be withdrawn.")) {
            response.sendRedirect(request.getContextPath() + "/ta/applications?withdrawError=status");
            return;
        }
        if (result.getErrors().contains("Application not found.")) {
            response.sendRedirect(request.getContextPath() + "/ta/applications?withdrawError=missing");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/ta/applications?withdrawError=1");
    }
}
