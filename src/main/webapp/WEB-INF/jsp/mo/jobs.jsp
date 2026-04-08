<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">MO Jobs</span>
    <h2 class="card-title">My Posted Jobs</h2>
    <p class="card-subtitle">Manage your own job postings, update requirements, and close jobs when they are no longer active.</p>

    <c:if test="${param.statusUpdated eq '1'}">
        <div class="success">Job status updated successfully.</div>
    </c:if>

    <c:if test="${param.notFound eq '1'}">
        <div class="error">The requested job could not be found or does not belong to your account.</div>
    </c:if>

    <c:if test="${param.draftSaved eq '1'}">
        <div class="success">Draft saved successfully.</div>
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
                                <h3><c:out value="${empty job.title ? 'Untitled draft job' : job.title}"/></h3>
                                <p class="muted">
                                    <c:out value="${empty job.moduleCode ? 'Module code not set' : job.moduleCode}"/>
                                    |
                                    <c:out value="${empty job.weeklyHours ? 'Hours not set' : job.weeklyHours}"/>
                                    hours/week
                                </p>
                            </div>
                            <c:choose>
                                <c:when test="${job.draft}">
                                    <span class="status-badge status-draft">DRAFT</span>
                                </c:when>
                                <c:when test="${job.open}">
                                    <span class="status-badge status-open">OPEN</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-closed">CLOSED</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <p><c:out value="${empty job.description ? 'No description has been written for this draft yet.' : job.description}"/></p>

                        <c:if test="${job.draft}">
                            <p class="helper">This draft is visible only to you and will not appear in TA job browsing until it is published.</p>
                        </c:if>

                        <div class="job-section">
                            <strong>Required skills</strong>
                            <c:choose>
                                <c:when test="${empty job.requiredSkills}">
                                    <p class="helper">No required skills selected yet.</p>
                                </c:when>
                                <c:otherwise>
                                    <div class="tag-list">
                                        <c:forEach items="${job.requiredSkills}" var="skill">
                                            <span class="tag">${skill}</span>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="actions-row">
                            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/jobs/view?jobId=${job.id}">View</a>
                            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/jobs/edit?jobId=${job.id}">
                                <c:out value="${job.draft ? 'Continue Draft' : 'Edit'}"/>
                            </a>
                            <c:if test="${not job.draft}">
                                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/mo/jobs/status" data-confirm="${job.open ? 'Close this job posting now?' : 'Reopen this job posting now?'}">
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
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
