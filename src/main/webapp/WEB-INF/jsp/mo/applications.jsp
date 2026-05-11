<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">MO Applications</span>
    <h2 class="card-title">Application Review Workspace</h2>
    <p class="card-subtitle">Review submitted candidates for your jobs, inspect their CVs, and move applications through the shortlist or reject stage.</p>

    <c:if test="${param.notFound eq '1'}">
        <div class="error">The requested application record could not be found under your jobs.</div>
    </c:if>

    <div class="summary-grid">
        <c:forEach items="${applicationSummary}" var="entry">
            <div class="summary-card">
                <strong>${entry.value}</strong>
                <span>${entry.key}</span>
            </div>
        </c:forEach>
    </div>

    <div class="context-bar">
        <c:choose>
            <c:when test="${not empty selectedJobId}">
                <span class="tag">Current scope: ${selectedJobTitle}</span>
            </c:when>
            <c:otherwise>
                <span class="tag">Current scope: all owned jobs</span>
            </c:otherwise>
        </c:choose>
        <c:if test="${hasActiveFilters}">
            <span class="tag tag-muted">Filtered result set</span>
        </c:if>
    </div>

    <c:if test="${hasActiveFilters}">
        <div class="tag-list-inline">
            <c:forEach items="${activeFilterChips}" var="chip">
                <span class="tag tag-muted">${chip}</span>
            </c:forEach>
        </div>
    </c:if>

    <div class="actions-row">
        <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications">All Applications</a>
        <c:forEach items="${jobs}" var="job">
            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications?jobId=${job.id}">
                <c:out value="${empty job.title ? job.moduleCode : job.title}"/>
            </a>
        </c:forEach>
    </div>

    <div class="card" style="margin-top: 18px; margin-bottom: 18px; padding: 20px;">
        <form method="get" action="${pageContext.request.contextPath}/mo/applications" data-auto-filter="mo-applications">
            <c:if test="${not empty selectedJobId}">
                <input type="hidden" name="jobId" value="${selectedJobId}">
            </c:if>

            <div class="form-grid">
                <div class="field-card">
                    <label for="mo-keyword">Keyword</label>
                    <input id="mo-keyword" name="keyword" value="${filterKeyword}"
                           placeholder="Search candidate name, student ID, email, module code"
                           data-auto-submit="debounce">
                </div>

                <div class="field-card">
                    <label for="mo-major">Major</label>
                    <select id="mo-major" name="major" data-auto-submit="immediate">
                        <option value="">All majors</option>
                        <c:forEach items="${availableMajors}" var="major">
                            <option value="${major}" <c:if test="${filterMajor eq major}">selected</c:if>>${major}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="field-card">
                    <label for="mo-status">Status</label>
                    <select id="mo-status" name="status" data-auto-submit="immediate">
                        <option value="">All statuses</option>
                        <c:forEach items="${statusOptions}" var="status">
                            <option value="${status}" <c:if test="${filterStatus eq status}">selected</c:if>>${status}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="job-section">
                <strong>Filter by candidate skills</strong>
                <div class="tag-list" data-skill-count="mo-filter-count">
                    <c:forEach items="${availableSkills}" var="skill">
                        <label class="tag tag-selectable">
                            <input type="checkbox" name="filterSkills" value="${skill}"
                                   <c:if test="${selectedFilterSkillLookup[skill]}">checked</c:if>
                                   data-auto-submit="immediate">
                            <span>${skill}</span>
                        </label>
                    </c:forEach>
                </div>
                <div id="mo-filter-count" class="selected-counter" style="margin-top: 8px;">0 selected</div>
            </div>

            <div class="actions-row">
                <button class="button-primary" type="submit">Apply Filters</button>
                <c:if test="${hasActiveFilters}">
                    <a class="button-secondary" href="${clearFilterHref}">Clear Filters</a>
                </c:if>
            </div>
        </form>
    </div>

    <c:choose>
        <c:when test="${empty applications}">
            <div class="empty-state">
                <c:choose>
                    <c:when test="${hasActiveFilters}">
                        <p>No application records match the current search and filter conditions.</p>
                        <div class="actions-row">
                            <a class="button-primary" href="${clearFilterHref}">Reset Filters</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p>No application records are available for the current scope.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Candidate</th>
                        <th>Student ID</th>
                        <th>Job</th>
                        <th>Skill Match</th>
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
                                <span class="muted">${applicationView.candidateEmail}</span><br>
                                <span class="helper">${applicationView.majorDisplay}</span>
                            </td>
                            <td>${applicationView.studentIdDisplay}</td>
                            <td>
                                <c:out value="${applicationView.job == null ? 'Job unavailable' : applicationView.job.title}"/><br>
                                <span class="muted"><c:out value="${applicationView.job == null ? '-' : applicationView.job.moduleCode}"/></span>
                            </td>
                            <td>
                                <span class="match-badge match-${applicationView.matchTone}">
                                    ${applicationView.matchLabel} (${applicationView.matchPercent}%)
                                </span>
                                <p class="helper">${applicationView.matchEvidenceSummary}</p>
                            </td>
                            <td><span class="${applicationView.statusTagClass}">${applicationView.statusLabel}</span></td>
                            <td>${applicationView.submittedAtDisplay}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${applicationView.hasCv}">
                                        <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications/cv/download?applicationId=${applicationView.application.id}">Download</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="tag tag-muted">Missing</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:url var="applicationDetailHref" value="/mo/applications/view">
                                    <c:param name="applicationId" value="${applicationView.application.id}"/>
                                    <c:if test="${not empty selectedJobId}">
                                        <c:param name="jobId" value="${selectedJobId}"/>
                                    </c:if>
                                </c:url>
                                <a class="button-secondary" href="${applicationDetailHref}">Open</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
