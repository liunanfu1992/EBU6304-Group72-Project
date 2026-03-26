package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.CvUploadResult;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.service.CvService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/ta/cv")
@MultipartConfig
public class CvServlet extends HttpServlet {
    private transient CvService cvService;

    @Override
    public void init() {
        this.cvService = new CvService(new ProfileRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        populateViewAttributes(request, cvService.getOrCreateProfile(currentUser));
        request.getRequestDispatcher(ViewPaths.TA_CV).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        Part cvPart = request.getPart("cvFile");

        try (InputStream inputStream = cvPart == null ? InputStream.nullInputStream() : cvPart.getInputStream()) {
            CvUploadResult result = cvService.uploadCv(
                    currentUser,
                    cvPart == null ? null : cvPart.getSubmittedFileName(),
                    cvPart == null ? 0 : cvPart.getSize(),
                    inputStream
            );

            if (!result.isSuccess()) {
                request.setAttribute("errors", result.getErrors());
                populateViewAttributes(request, result.getProfile());
                request.getRequestDispatcher(ViewPaths.TA_CV).forward(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/ta/cv?uploaded=1");
    }

    private void populateViewAttributes(HttpServletRequest request, Profile profile) {
        request.setAttribute("profile", profile);
        request.setAttribute("allowedCvTypes", cvService.getAllowedExtensionsDisplay());
        request.setAttribute("maxCvSizeMb", cvService.getMaxCvSizeMb());
    }
}
