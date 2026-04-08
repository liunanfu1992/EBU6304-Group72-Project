<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Applicant Detail</span>
    <div class="job-card-head">
        <div>
            <h2>${applicationView.candidateDisplayName}</h2>
            <p class="muted">${applicationView.candidateEmail} | ${applicationView.majorDisplay}</p>
        </div>
        <span class="${applicationView.statusTagClass}">${applicationView.statusLabel}</span>
    </div>

    <div class="metrics-row">
        <div class="metric-card">
            <strong>Student ID</strong>
            <span class="metric-label">${applicationView.studentIdDisplay}</span>
        </div>
        <div class="metric-card">
            <strong>Submitted</strong>
            <span class="metric-label">${applicationView.submittedAtDisplay}</span>
        </div>
        <div class="metric-card">
            <strong>CV Record</strong>
            <span class="metric-label">${applicationView.hasCv ? 'Uploaded' : 'Missing'}</span>
        </div>
    </div>
</div>

<div class="card">
    <h3 class="card-title">Applied Job</h3>
    <p class="helper">This section anchors the shared application record to the target job before shortlist/reject actions are attached.</p>
    <c:choose>
        <c:when test="${applicationView.job == null}">
            <p>The referenced job is no longer available.</p>
        </c:when>
        <c:otherwise>
            <div class="job-card">
                <div class="job-card-head">
                    <div>
                        <h3>${applicationView.job.title}</h3>
                        <p class="muted">${applicationView.job.moduleCode} | ${applicationView.job.weeklyHours} hours/week</p>
                    </div>
                </div>
                <p>${applicationView.job.description}</p>
                <div class="job-section">
                    <strong>Required skills</strong>
                    <div class="tag-list">
                        <c:forEach items="${applicationView.job.requiredSkills}" var="skill">
                            <span class="tag">${skill}</span>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <h3 class="card-title">Candidate Profile Snapshot</h3>
    <p class="helper">This is the shared review skeleton for Sprint 2. CV download and status action controls will plug into this page in the next implementation tasks.</p>
    <c:choose>
        <c:when test="${applicationView.profile == null}">
            <p>No TA profile record is available yet.</p>
        </c:when>
        <c:otherwise>
            <div class="job-section">
                <strong>Profile Skills</strong>
                <div class="tag-list">
                    <c:forEach items="${applicationView.profile.allSkills}" var="skill">
                        <span class="tag">${skill}</span>
                    </c:forEach>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="actions-row">
        <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications">Back to Applications</a>
    </div>
</div>
<%@ include file="../common/footer.jspf" %>
