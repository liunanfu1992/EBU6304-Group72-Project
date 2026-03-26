<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <div class="job-card-head">
        <div>
            <h2>${job.title}</h2>
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

    <c:if test="${param.updated eq '1'}">
        <div class="success">Job updated successfully.</div>
    </c:if>

    <c:if test="${param.statusUpdated eq '1'}">
        <div class="success">Job status updated successfully.</div>
    </c:if>

    <c:if test="${param.notFound eq '1'}">
        <div class="error">The requested job could not be found or does not belong to your account.</div>
    </c:if>

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
        <a class="button-secondary" href="${pageContext.request.contextPath}/mo/jobs">Back to My Jobs</a>
        <a class="button-secondary" href="${pageContext.request.contextPath}/mo/jobs/edit?jobId=${job.id}">Edit Job</a>
        <form class="inline-form" method="post" action="${pageContext.request.contextPath}/mo/jobs/status">
            <input type="hidden" name="jobId" value="${job.id}">
            <input type="hidden" name="source" value="detail">
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

<div class="card">
    <h2>Candidate Preview</h2>
    <p class="muted">This preview ranks TA profiles by predefined-skill coverage for the current job.</p>

    <c:choose>
        <c:when test="${empty candidateMatches}">
            <p>No completed TA profiles are available for preview yet.</p>
        </c:when>
        <c:otherwise>
            <div class="candidate-grid">
                <c:forEach items="${candidateMatches}" var="candidate">
                    <div class="candidate-card">
                        <div class="job-card-head">
                            <div>
                                <h3>${candidate.displayName}</h3>
                                <p class="muted">${candidate.profile.email} | ${candidate.profile.major}</p>
                            </div>
                            <span class="match-badge match-${candidate.matchTone}">
                                ${candidate.matchLabel} (${candidate.matchPercent}%)
                            </span>
                        </div>

                        <div class="job-section">
                            <strong>Candidate skills</strong>
                            <div class="tag-list">
                                <c:forEach items="${candidate.profile.allSkills}" var="skill">
                                    <span class="tag">${skill}</span>
                                </c:forEach>
                            </div>
                        </div>

                        <c:if test="${not empty candidate.matchedSkills}">
                            <div class="job-section">
                                <strong>Matched required skills</strong>
                                <div class="tag-list">
                                    <c:forEach items="${candidate.matchedSkills}" var="skill">
                                        <span class="tag">${skill}</span>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${not empty candidate.missingSkills}">
                            <div class="job-section">
                                <strong>Missing required skills</strong>
                                <div class="tag-list">
                                    <c:forEach items="${candidate.missingSkills}" var="skill">
                                        <span class="tag tag-muted">${skill}</span>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
