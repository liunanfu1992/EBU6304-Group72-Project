package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.MoApplicationView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import com.group72.tarecruitment.service.CvService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mo/applications/cv/download")
public class MoApplicationCvDownloadServlet extends HttpServlet {
    private transient ApplicationService applicationService;
    private transient CvService cvService;

    @Override
    public void init() {
        ProfileRepository profileRepository = new ProfileRepository();
        this.applicationService = new ApplicationService(
                new ApplicationRepository(),
                new JobRepository(),
                new UserRepository(),
                profileRepository
        );
        this.cvService = new CvService(profileRepository);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String applicationId = request.getParameter("applicationId");

        Optional<MoApplicationView> applicationView = applicationService.findOwnedApplicationView(applicationId, currentUser.getId());
        if (applicationView.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested candidate CV is not available under your jobs.");
            return;
        }
        if (!applicationView.get().hasCv()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No uploaded CV is available for this application.");
            return;
        }

        Optional<byte[]> cvBytes = cvService.readStoredCvBytes(applicationView.get().getProfile());
        if (cvBytes.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No uploaded CV is available for this application.");
            return;
        }

        String downloadFileName = cvService.getDownloadFileName(applicationView.get().getProfile());
        response.setContentType(cvService.getContentType(downloadFileName));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setContentLengthLong(cvBytes.get().length);
        response.getOutputStream().write(cvBytes.get());
    }
}
