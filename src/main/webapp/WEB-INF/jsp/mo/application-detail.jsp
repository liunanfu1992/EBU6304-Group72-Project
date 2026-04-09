<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Applicant Detail</span>

    <c:if test="${param.reviewUpdated eq 'shortlisted'}">
        <div class="success">The application status was updated to SHORTLISTED.</div>
    </c:if>
    <c:if test="${param.reviewUpdated eq 'rejected'}">
        <div class="success">The application status was updated to REJECTED.</div>
    </c:if>
    <c:if test="${param.reviewError eq 'withdrawn'}">
        <div class="error">This application was withdrawn by the TA and can no longer be processed.</div>
    </c:if>
    <c:if test="${param.reviewError eq 'missing'}">
        <div class="error">The requested application record could not be found under your jobs.</div>
    </c:if>
    <c:if test="${param.reviewError eq 'invalid' or param.reviewError eq '1'}">
        <div class="error">The application status could not be updated. Please try again.</div>
    </c:if>

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
    <p class="helper">This section anchors the shared application record to the target job so the review decision stays tied to the correct module posting.</p>
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
                <c:if test="${applicationView.jobClosed}">
                    <div class="warning">This job is closed to new TA applications, but existing submitted applications can still be reviewed here.</div>
                </c:if>
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
    <p class="helper">Use this snapshot to inspect the candidate's submitted details before making a review decision.</p>
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
        <c:if test="${applicationView.hasCv}">
            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications/cv/download?applicationId=${applicationView.application.id}">Download CV</a>
        </c:if>
        <c:if test="${applicationView.canShortlist}">
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/mo/applications/status"
                  data-confirm="Mark this candidate as SHORTLISTED for the current job?">
                <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                <input type="hidden" name="status" value="SHORTLISTED">
                <button class="button-primary" type="submit">Shortlist</button>
            </form>
        </c:if>
        <c:if test="${applicationView.canReject}">
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/mo/applications/status"
                  data-confirm="Mark this candidate as REJECTED for the current job?">
                <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                <input type="hidden" name="status" value="REJECTED">
                <button class="button-warning" type="submit">Reject</button>
            </form>
        </c:if>
        <c:if test="${applicationView.reviewLocked}">
            <span class="tag tag-muted">Review locked after TA withdrawal</span>
        </c:if>
        <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications">Back to Applications</a>
    </div>
</div>
<%@ include file="../common/footer.jspf" %>
