package com.group72.tarecruitment.servlet.mo;

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

@WebServlet("/mo/applications/interview/outcome")
public class MoInterviewOutcomeServlet extends HttpServlet {
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
        String jobId = request.getParameter("jobId");

        ApplicationActionResult result = applicationService.recordInterviewOutcome(
                applicationId,
                currentUser.getId(),
                request.getParameter("finalStatus"),
                request.getParameter("interviewOutcomeNotes")
        );

        String suffix = buildDetailSuffix(applicationId, jobId);
        response.sendRedirect(request.getContextPath() + "/mo/applications/view" + suffix
                + (result.isSuccess() ? "&outcomeRecorded=1" : "&outcomeError=" + errorFlag(result)));
    }

    private String buildDetailSuffix(String applicationId, String jobId) {
        String suffix = "?applicationId=" + encode(applicationId);
        if (jobId != null && !jobId.isBlank()) {
            suffix += "&jobId=" + encode(jobId);
        }
        return suffix;
    }

    private String errorFlag(ApplicationActionResult result) {
        return encode(result.getErrors().isEmpty() ? "1" : result.getErrors().get(0));
    }

    private String encode(String value) {
        return value == null ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
