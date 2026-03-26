package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.service.CvService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ta/cv/download")
public class CvDownloadServlet extends HttpServlet {
    private transient CvService cvService;

    @Override
    public void init() {
        this.cvService = new CvService(new ProfileRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        Profile profile = cvService.getOrCreateProfile(currentUser);
        Optional<Path> cvFile = cvService.resolveStoredCv(profile);

        if (cvFile.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No uploaded CV is available for download.");
            return;
        }

        String downloadFileName = cvService.getDownloadFileName(profile);
        response.setContentType(cvService.getContentType(downloadFileName));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setContentLengthLong(Files.size(cvFile.get()));

        Files.copy(cvFile.get(), response.getOutputStream());
    }
}
