package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.Application;
import com.group72.tarecruitment.model.TaApplicationView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ta/applications")
public class TaApplicationsServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        List<TaApplicationView> applications = applicationService.listTaApplicationViews(currentUser.getId());
        request.setAttribute("applications", applications);
        request.setAttribute("applicationSummary", buildStatusSummary(applications));
        request.getRequestDispatcher(ViewPaths.TA_APPLICATIONS).forward(request, response);
    }

    private Map<String, Integer> buildStatusSummary(List<TaApplicationView> applications) {
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("Total", applications.size());
        summary.put("Pending", 0);
        summary.put("Shortlisted", 0);
        summary.put("Rejected", 0);
        summary.put("Withdrawn", 0);

        for (TaApplicationView applicationView : applications) {
            Application application = applicationView.getApplication();
            if (application == null) {
                continue;
            }
            if (application.isPending()) {
                summary.put("Pending", summary.get("Pending") + 1);
            } else if (application.isShortlisted()) {
                summary.put("Shortlisted", summary.get("Shortlisted") + 1);
            } else if (application.isRejected()) {
                summary.put("Rejected", summary.get("Rejected") + 1);
            } else if (application.isWithdrawn()) {
                summary.put("Withdrawn", summary.get("Withdrawn") + 1);
            }
        }

        return summary;
    }
}
