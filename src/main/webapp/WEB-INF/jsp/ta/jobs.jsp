<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>Available Jobs</h2>
    <p class="muted">The TA job listing module is wired to the shared job repository.</p>

    <c:choose>
        <c:when test="${empty jobs}">
            <p>No jobs have been posted yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Module</th>
                    <th>Weekly Hours</th>
                    <th>Required Skills</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${jobs}" var="job">
                    <tr>
                        <td>${job.title}</td>
                        <td>${job.moduleCode}</td>
                        <td>${job.weeklyHours}</td>
                        <td>
                            <c:forEach items="${job.requiredSkills}" var="skill">
                                <span class="tag">${skill}</span>
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
