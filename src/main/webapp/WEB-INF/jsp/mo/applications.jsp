<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">MO Applications</span>
    <h2 class="card-title">Application Review Workspace</h2>
    <p class="card-subtitle">This shared Sprint 2 workspace groups the application records for jobs you own.</p>

    <c:if test="${param.notFound eq '1'}">
        <div class="error">The requested application record could not be found under your jobs.</div>
    </c:if>

    <div class="info">Jiayang's backbone wires the shared applicant views here. Search, filter, and review actions will attach on top of this workspace in the next tasks.</div>

    <div class="actions-row">
        <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications">All Applications</a>
        <c:forEach items="${jobs}" var="job">
            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications?jobId=${job.id}">
                <c:out value="${empty job.title ? job.moduleCode : job.title}"/>
            </a>
        </c:forEach>
    </div>

    <c:choose>
        <c:when test="${empty applications}">
            <p>No application records are available for the current scope.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Candidate</th>
                    <th>Student ID</th>
                    <th>Job</th>
                    <th>Status</th>
                    <th>Submitted</th>
                    <th>CV</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${applications}" var="applicationView">
                    <tr>
                        <td>
                            <strong>${applicationView.candidateDisplayName}</strong><br>
                            <span class="muted">${applicationView.candidateEmail}</span>
                        </td>
                        <td>${applicationView.studentIdDisplay}</td>
                        <td>
                            <c:out value="${applicationView.job == null ? 'Job unavailable' : applicationView.job.title}"/><br>
                            <span class="muted"><c:out value="${applicationView.job == null ? '-' : applicationView.job.moduleCode}"/></span>
                        </td>
                        <td><span class="${applicationView.statusTagClass}">${applicationView.statusLabel}</span></td>
                        <td>${applicationView.submittedAtDisplay}</td>
                        <td>
                            <c:choose>
                                <c:when test="${applicationView.hasCv}">
                                    <span class="tag">Uploaded</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag tag-muted">Missing</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications/view?applicationId=${applicationView.application.id}">Open</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
