<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Job Match</span>
    <h2 class="card-title">Available Jobs</h2>
    <p class="card-subtitle">Browse current TA openings and preview the predefined-skill match against your profile.</p>

    <c:choose>
        <c:when test="${empty profile.selectedSkills}">
            <div class="warning">
                Your profile does not have any predefined skills yet, so the match score cannot help you much.
                Update your profile first to unlock stronger recommendations.
            </div>
        </c:when>
        <c:otherwise>
            <div class="info">
                <strong>Your predefined skills:</strong>
                <span class="tag-list-inline">
                    <c:forEach items="${profile.selectedSkills}" var="skill">
                        <span class="tag">${skill}</span>
                    </c:forEach>
                </span>
            </div>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${empty jobMatches}">
            <p>No jobs have been posted yet.</p>
        </c:when>
        <c:otherwise>
            <div class="job-list">
                <c:forEach items="${jobMatches}" var="match">
                    <div class="job-card">
                        <div class="job-card-head">
                            <div>
                                <h3>${match.job.title}</h3>
                                <p class="muted">${match.job.moduleCode} | ${match.job.weeklyHours} hours/week</p>
                            </div>
                            <span class="match-badge match-${match.matchTone}">${match.matchLabel} (${match.matchPercent}%)</span>
                        </div>

                        <p>${match.job.description}</p>

                        <div class="job-section">
                            <strong>Required skills</strong>
                            <div class="tag-list">
                                <c:forEach items="${match.job.requiredSkills}" var="skill">
                                    <span class="tag">${skill}</span>
                                </c:forEach>
                            </div>
                        </div>

                        <c:if test="${not empty match.matchedSkills}">
                            <div class="job-section">
                                <strong>Matched skills</strong>
                                <div class="tag-list">
                                    <c:forEach items="${match.matchedSkills}" var="skill">
                                        <span class="tag">${skill}</span>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${not empty match.missingSkills}">
                            <div class="job-section">
                                <strong>Missing predefined skills</strong>
                                <div class="tag-list">
                                    <c:forEach items="${match.missingSkills}" var="skill">
                                        <span class="tag tag-muted">${skill}</span>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <p class="helper">Match score only considers predefined skills shared by TA profiles and job postings in Sprint 1.</p>
                        <div class="actions-row">
                            <a class="button-secondary" href="${pageContext.request.contextPath}/ta/profile">Refine My Skills</a>
                            <a class="button-secondary" href="${pageContext.request.contextPath}/ta/cv">Update My CV</a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
