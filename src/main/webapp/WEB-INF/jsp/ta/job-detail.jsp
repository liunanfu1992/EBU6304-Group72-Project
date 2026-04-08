<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Job Detail</span>
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
                <span class="tag">${skill}</span>
            </c:forEach>
        </div>
    </div>

    <c:if test="${not empty jobMatch.matchedSkills}">
        <div class="job-section">
            <strong>Matched skills</strong>
            <div class="tag-list">
                <c:forEach items="${jobMatch.matchedSkills}" var="skill">
                    <span class="tag">${skill}</span>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <c:if test="${not empty jobMatch.missingSkills}">
        <div class="job-section">
            <strong>Missing predefined skills</strong>
            <div class="tag-list">
                <c:forEach items="${jobMatch.missingSkills}" var="skill">
                    <span class="tag tag-muted">${skill}</span>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <p class="helper">Sprint 1 currently supports browsing and match preview only. Application submission will be added in a later sprint.</p>

    <div class="actions-row">
        <a class="button-secondary" href="${pageContext.request.contextPath}/ta/jobs">Back to Job List</a>
        <a class="button-secondary" href="${pageContext.request.contextPath}/ta/profile">Refine My Skills</a>
        <a class="button-secondary" href="${pageContext.request.contextPath}/ta/cv">Update My CV</a>
    </div>
</div>
<%@ include file="../common/footer.jspf" %>
