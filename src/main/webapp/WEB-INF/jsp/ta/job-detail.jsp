<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Job Detail</span>

    <c:if test="${param.alreadyApplied eq '1'}">
        <div class="warning">You already have an application record for this job. Check your application dashboard for the latest status.</div>
    </c:if>
    <c:if test="${param.jobUnavailable eq '1'}">
        <div class="error">This job is no longer open for new applications.</div>
    </c:if>
    <c:if test="${param.applyError eq '1'}">
        <div class="error">The application could not be submitted. Please try again from this page.</div>
    </c:if>

    <div class="job-card-head">
        <div>
            <h2>${jobMatch.job.title}</h2>
            <p class="muted">${jobMatch.job.moduleCode} | ${jobMatch.job.weeklyHours} hours/week</p>
            <p class="helper">
                Module Owner: ${jobMatch.moduleOwnerDisplayName}
                <c:if test="${not empty jobMatch.moduleOwnerEmail}">
                    | ${jobMatch.moduleOwnerEmail}
                </c:if>
            </p>
        </div>
        <span class="match-badge match-${jobMatch.matchTone}">${jobMatch.matchLabel} (${jobMatch.matchPercent}%)</span>
    </div>

    <p>${jobMatch.job.description}</p>

    <div class="job-section">
        <strong>Required skills</strong>
        <div class="tag-list">
            <c:forEach items="${jobMatch.job.requiredSkills}" var="skill">
                <c:choose>
                    <c:when test="${jobMatch.isMatchedSkill(skill)}">
                        <span class="tag tag-match">${skill}</span>
                    </c:when>
                    <c:when test="${jobMatch.isMissingSkill(skill)}">
                        <span class="tag tag-missing">${skill}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="tag">${skill}</span>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
        <p class="helper">Green skills are already in your profile. Red skills are missing from your predefined skill set.</p>
    </div>

    <c:if test="${not empty jobMatch.matchedSkills}">
        <div class="job-section">
            <strong>Matched skills</strong>
            <div class="tag-list">
                <c:forEach items="${jobMatch.matchedSkills}" var="skill">
                    <span class="tag tag-match">${skill}</span>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <c:if test="${not empty jobMatch.missingSkills}">
        <div class="job-section">
            <strong>Missing predefined skills</strong>
            <div class="tag-list">
                <c:forEach items="${jobMatch.missingSkills}" var="skill">
                    <span class="tag tag-missing">${skill}</span>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty existingApplication}">
            <div class="info">
                You can submit a TA application from this page. The request will be recorded immediately with status
                <strong>Pending</strong>.
            </div>
        </c:when>
        <c:otherwise>
            <div class="info">
                Your current application status for this job is
                <strong>${existingApplication.statusLabel}</strong>,
                submitted on ${existingApplication.submittedAtDisplay}.
            </div>
        </c:otherwise>
    </c:choose>

    <div class="actions-row">
        <c:choose>
            <c:when test="${empty existingApplication}">
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/ta/applications/apply"
                      data-confirm="Submit your application for this TA job now?">
                    <input type="hidden" name="jobId" value="${jobMatch.job.id}">
                    <button class="button-primary" type="submit">Apply for This Job</button>
                </form>
            </c:when>
            <c:otherwise>
                <a class="button-primary" href="${pageContext.request.contextPath}/ta/applications">View My Application</a>
            </c:otherwise>
        </c:choose>
        <a class="button-secondary" href="${pageContext.request.contextPath}/ta/jobs">Back to Job List</a>
        <a class="button-secondary" href="${pageContext.request.contextPath}/ta/profile">Refine My Skills</a>
        <a class="button-secondary" href="${pageContext.request.contextPath}/ta/cv">Update My CV</a>
    </div>
</div>
<%@ include file="../common/footer.jspf" %>
