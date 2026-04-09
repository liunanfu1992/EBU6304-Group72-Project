package com.group72.tarecruitment.servlet.mo;

import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mo/applications/view")
public class MoApplicationDetailServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String applicationId = request.getParameter("applicationId");
        String jobId = request.getParameter("jobId");

        var applicationView = applicationService.findOwnedApplicationView(applicationId, currentUser.getId());
        if (applicationView.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/mo/applications?notFound=1");
            return;
        }

        request.setAttribute("applicationView", applicationView.get());
        request.setAttribute("returnJobId", jobId == null ? "" : jobId);
        request.setAttribute("backToListHref", request.getContextPath() + "/mo/applications"
                + ((jobId == null || jobId.isBlank()) ? "" : "?jobId=" + jobId));
        request.getRequestDispatcher(ViewPaths.MO_APPLICATION_DETAIL).forward(request, response);
    }
}
