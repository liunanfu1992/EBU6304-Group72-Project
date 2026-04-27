package com.group72.tarecruitment.servlet.ta;

import com.group72.tarecruitment.model.TaApplicationView;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ApplicationRepository;
import com.group72.tarecruitment.repository.json.JobRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.ApplicationService;
import com.group72.tarecruitment.util.ViewPaths;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ta/interviews")
public class TaInterviewsServlet extends HttpServlet {
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
        List<TaApplicationView> interviews = applicationService.listTaInterviewViews(currentUser.getId());
        request.setAttribute("interviews", interviews);
        request.getRequestDispatcher(ViewPaths.TA_INTERVIEWS).forward(request, response);
    }
}
