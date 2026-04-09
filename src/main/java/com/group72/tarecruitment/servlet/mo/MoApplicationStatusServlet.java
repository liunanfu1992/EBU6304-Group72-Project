package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.model.ApplicationActionResult;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
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

@WebServlet("/mo/applications/status")
public class MoApplicationStatusServlet extends HttpServlet {
    private transient ApplicationService applicationService;

    @Override
    public void init() {
        this.applicationService = new ApplicationService(
                new ApplicationRepository(),
                new JobRepository(),
                new UserRepository(),
                new ProfileRepository()
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String applicationId = request.getParameter("applicationId");
        String status = request.getParameter("status");
        String jobId = request.getParameter("jobId");

        ApplicationActionResult result = applicationService.updateApplicationStatus(applicationId, currentUser.getId(), status);
        String encodedApplicationId = encode(applicationId);
        String detailSuffix = (jobId == null || jobId.isBlank()) ? "" : "&jobId=" + encode(jobId);

        if (result.isSuccess()) {
            String statusFlag = Application.STATUS_SHORTLISTED.equalsIgnoreCase(status)
                    ? "shortlisted"
                    : Application.STATUS_REJECTED.equalsIgnoreCase(status) ? "rejected" : "updated";
            response.sendRedirect(request.getContextPath() + "/mo/applications/view?applicationId="
                    + encodedApplicationId + detailSuffix + "&reviewUpdated=" + statusFlag);
            return;
        }

        String errorFlag = "1";
        if (result.getErrors().contains("Withdrawn applications can no longer be processed.")) {
            errorFlag = "withdrawn";
        } else if (result.getErrors().contains("Application not found.")) {
            errorFlag = "missing";
        } else if (result.getErrors().contains("Invalid application status.")) {
            errorFlag = "invalid";
        }

        response.sendRedirect(request.getContextPath() + "/mo/applications/view?applicationId="
                + encodedApplicationId + detailSuffix + "&reviewError=" + errorFlag);
    }

    private String encode(String value) {
        return value == null ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
