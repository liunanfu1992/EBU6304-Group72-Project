<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Job Match</span>
    <h2 class="card-title">Available Jobs</h2>
    <p class="card-subtitle">Browse current TA openings, search by keywords, and narrow the list with predefined skill filters.</p>

    <c:if test="${param.notFound eq '1'}">
        <div class="error">The requested job is no longer available for browsing.</div>
    </c:if>

    <div class="card" style="margin-top: 18px; margin-bottom: 18px; padding: 20px;">
        <form method="get" action="${pageContext.request.contextPath}/ta/jobs" data-auto-filter="job-search">
            <div class="metrics-row">
                <div>
                    <label for="keyword">Keyword Search</label>
                    <input id="keyword" name="keyword" value="${keyword}" placeholder="Search title, module code, description, skills, or module owner"
                           data-auto-submit="debounce">
                </div>
                <div>
                    <label>Selected Skill Filters</label>
                    <div class="helper">
                        Use multiple predefined skills to keep only jobs that require all selected skills.
                    </div>
                    <div id="ta-filter-count" class="selected-counter" style="margin-top: 8px;">0 selected</div>
                </div>
            </div>

            <div class="job-section">
                <strong>Filter by predefined skills</strong>
                <div class="tag-list" data-skill-count="ta-filter-count">
                    <c:forEach items="${availableSkills}" var="skill">
                        <label class="tag tag-selectable">
                            <input type="checkbox" name="filterSkills" value="${skill}"
                                   <c:if test="${selectedFilterSkillLookup[skill]}">checked</c:if>
                                   data-auto-submit="immediate">
                            <span>${skill}</span>
                        </label>
                    </c:forEach>
                </div>
            </div>

            <div class="actions-row">
                <button class="button-primary" type="submit">Apply Filters</button>
                <c:if test="${hasActiveFilters}">
                    <a class="button-secondary" href="${pageContext.request.contextPath}/ta/jobs">Clear Filters</a>
                </c:if>
            </div>
        </form>
    </div>

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
            <c:choose>
                <c:when test="${hasActiveFilters}">
                    <p>No jobs match the current keyword or skill filters.</p>
                </c:when>
                <c:otherwise>
                    <p>No jobs have been posted yet.</p>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <div class="job-list">
                <c:forEach items="${jobMatches}" var="match">
                    <div class="job-card">
                        <div class="job-card-head">
                            <div>
                                <h3>${match.job.title}</h3>
                                <p class="muted">${match.job.moduleCode} | ${match.job.weeklyHours} hours/week</p>
                                <p class="helper">
                                    Module Owner: ${match.moduleOwnerDisplayName}
                                    <c:if test="${not empty match.moduleOwnerEmail}">
                                        | ${match.moduleOwnerEmail}
                                    </c:if>
                                </p>
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
                            <a class="button-secondary" href="${pageContext.request.contextPath}/ta/jobs/view?jobId=${match.job.id}">View Details</a>
                            <a class="button-secondary" href="${pageContext.request.contextPath}/ta/profile">Refine My Skills</a>
                            <a class="button-secondary" href="${pageContext.request.contextPath}/ta/cv">Update My CV</a>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${totalPages gt 1}">
                <div class="actions-row">
                    <c:if test="${hasPreviousPage}">
                        <a class="button-secondary" href="${pageContext.request.contextPath}/ta/jobs?page=${previousPage}">Previous Page</a>
                    </c:if>
                    <span class="selected-counter">Page ${currentPage} of ${totalPages}</span>
                    <c:if test="${hasNextPage}">
                        <a class="button-secondary" href="${pageContext.request.contextPath}/ta/jobs?page=${nextPage}">Next Page</a>
                    </c:if>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
