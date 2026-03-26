<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>My Posted Jobs</h2>
    <p class="muted">Manage your own job postings, update requirements, and close jobs when they are no longer active.</p>

    <c:if test="${param.statusUpdated eq '1'}">
        <div class="success">Job status updated successfully.</div>
    </c:if>

    <c:if test="${param.notFound eq '1'}">
        <div class="error">The requested job could not be found or does not belong to your account.</div>
    </c:if>

    <div class="actions-row">
        <a class="button-secondary" href="${pageContext.request.contextPath}/mo/jobs/new">Post Another Job</a>
    </div>

    <c:choose>
        <c:when test="${empty jobs}">
            <p>You have not posted any jobs yet.</p>
        </c:when>
        <c:otherwise>
            <div class="job-list">
                <c:forEach items="${jobs}" var="job">
                    <div class="job-card">
                        <div class="job-card-head">
                            <div>
                                <h3>${job.title}</h3>
                                <p class="muted">${job.moduleCode} | ${job.weeklyHours} hours/week</p>
                            </div>
                            <c:choose>
                                <c:when test="${job.open}">
                                    <span class="status-badge status-open">OPEN</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-closed">CLOSED</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <p>${job.description}</p>

                        <div class="job-section">
                            <strong>Required skills</strong>
                            <div class="tag-list">
                                <c:forEach items="${job.requiredSkills}" var="skill">
                                    <span class="tag">${skill}</span>
                                </c:forEach>
                            </div>
                        </div>

                        <div class="actions-row">
                            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/jobs/view?jobId=${job.id}">View</a>
                            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/jobs/edit?jobId=${job.id}">Edit</a>
                            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/mo/jobs/status">
                                <input type="hidden" name="jobId" value="${job.id}">
                                <c:choose>
                                    <c:when test="${job.open}">
                                        <input type="hidden" name="status" value="CLOSED">
                                        <button class="button-warning" type="submit">Close Job</button>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" name="status" value="OPEN">
                                        <button class="button-secondary" type="submit">Reopen Job</button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
