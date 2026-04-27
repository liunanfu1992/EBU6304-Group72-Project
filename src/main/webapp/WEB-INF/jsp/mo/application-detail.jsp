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
    <c:if test="${param.interviewScheduled eq '1'}">
        <div class="success">The interview schedule was saved for this shortlisted candidate.</div>
    </c:if>
    <c:choose>
        <c:when test="${param.interviewError eq 'status'}">
            <div class="error">Only shortlisted applications can be scheduled for interview.</div>
        </c:when>
        <c:when test="${param.interviewError eq 'time'}">
            <div class="error">Interview date and time are required.</div>
        </c:when>
        <c:when test="${param.interviewError eq 'location'}">
            <div class="error">Add either an interview location or a meeting link.</div>
        </c:when>
        <c:when test="${param.interviewError eq 'missing'}">
            <div class="error">The requested application record could not be found under your jobs.</div>
        </c:when>
        <c:when test="${not empty param.interviewError}">
            <div class="error">The interview schedule could not be saved. Please try again.</div>
        </c:when>
    </c:choose>
    <c:if test="${param.outcomeRecorded eq '1'}">
        <div class="success">The interview outcome and final decision were recorded.</div>
    </c:if>
    <c:if test="${not empty param.outcomeError}">
        <div class="error">The interview outcome could not be recorded. ${param.outcomeError}</div>
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
    <h3 class="card-title">Interview Schedule</h3>
    <p class="helper">Sprint 3 scheduling is available after the candidate has been shortlisted.</p>

    <div class="detail-grid">
        <div class="detail-panel">
            <h4>Current Schedule</h4>
            <p><strong>Time:</strong> ${applicationView.interviewStartDisplay}</p>
            <p><strong>Location:</strong> ${applicationView.interviewLocationDisplay}</p>
            <p><strong>Meeting Link:</strong> ${applicationView.interviewLinkDisplay}</p>
            <p><strong>TA Attendance:</strong> ${applicationView.attendanceLabel}</p>
        </div>

        <div class="detail-panel">
            <h4>Schedule or Update Interview</h4>
            <c:choose>
                <c:when test="${applicationView.canScheduleInterview}">
                    <form method="post" action="${pageContext.request.contextPath}/mo/applications/interview/schedule"
                          data-confirm="Save this interview schedule for the shortlisted candidate?">
                        <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                        <input type="hidden" name="jobId" value="${returnJobId}">
                        <label for="interviewStart">Date and time</label>
                        <input id="interviewStart" type="datetime-local" name="interviewStart" required>

                        <label for="interviewLocation">Location</label>
                        <input id="interviewLocation" type="text" name="interviewLocation"
                               value="${applicationView.application.interviewLocation}">

                        <label for="interviewLink">Meeting link</label>
                        <input id="interviewLink" type="url" name="interviewLink"
                               value="${applicationView.application.interviewLink}">

                        <button class="button-primary" type="submit">Save Interview</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <p class="muted">Interview scheduling is available only for shortlisted applications.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="card">
    <h3 class="card-title">Interview Outcome</h3>
    <p class="helper">Record final interview notes and move the application to the final decision stage.</p>
    <c:choose>
        <c:when test="${applicationView.canRecordOutcome}">
            <form method="post" action="${pageContext.request.contextPath}/mo/applications/interview/outcome"
                  data-confirm="Record this final interview outcome?">
                <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                <input type="hidden" name="jobId" value="${returnJobId}">

                <label for="finalStatus">Final decision</label>
                <select id="finalStatus" name="finalStatus" required>
                    <option value="OFFERED">Offered</option>
                    <option value="REJECTED">Rejected</option>
                </select>

                <label for="interviewOutcomeNotes">Outcome notes</label>
                <textarea id="interviewOutcomeNotes" name="interviewOutcomeNotes" rows="4">${applicationView.application.interviewOutcomeNotes}</textarea>

                <button class="button-primary" type="submit">Record Outcome</button>
            </form>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty applicationView.application.interviewOutcomeNotes}">
                <div class="detail-panel">
                    <h4>Recorded Notes</h4>
                    <p>${applicationView.application.interviewOutcomeNotes}</p>
                </div>
            </c:if>
            <p class="muted">A scheduled interview is required before recording the final outcome.</p>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <h3 class="card-title">Candidate Profile Snapshot</h3>
    <p class="helper">Use this snapshot to inspect the candidate's submitted details before making a review decision.</p>
    <div class="detail-grid">
        <div class="detail-panel">
            <h4>Profile Skills</h4>
            <c:choose>
                <c:when test="${applicationView.profile == null}">
                    <p class="muted">No TA profile record is available yet.</p>
                </c:when>
                <c:when test="${empty applicationView.profile.allSkills}">
                    <p class="muted">This candidate has not selected any skills yet.</p>
                </c:when>
                <c:otherwise>
                    <div class="tag-list">
                        <c:forEach items="${applicationView.profile.allSkills}" var="skill">
                            <span class="tag">${skill}</span>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="detail-panel">
            <h4>Skill Fit Snapshot</h4>
            <c:if test="${not empty applicationView.matchedSkills}">
                <strong>Matched</strong>
                <div class="tag-list">
                    <c:forEach items="${applicationView.matchedSkills}" var="skill">
                        <span class="tag">${skill}</span>
                    </c:forEach>
                </div>
            </c:if>
            <c:if test="${not empty applicationView.missingSkills}">
                <strong>Missing</strong>
                <div class="tag-list">
                    <c:forEach items="${applicationView.missingSkills}" var="skill">
                        <span class="tag tag-muted">${skill}</span>
                    </c:forEach>
                </div>
            </c:if>
            <c:if test="${empty applicationView.matchedSkills and empty applicationView.missingSkills}">
                <p class="muted">No structured job skill requirements are attached to this application.</p>
            </c:if>
        </div>
    </div>

    <div class="actions-row">
        <c:if test="${applicationView.hasCv}">
            <a class="button-secondary" href="${pageContext.request.contextPath}/mo/applications/cv/download?applicationId=${applicationView.application.id}">Download CV</a>
        </c:if>
        <c:if test="${applicationView.canShortlist}">
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/mo/applications/status"
                  data-confirm="Mark this candidate as SHORTLISTED for the current job?">
                <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                <input type="hidden" name="jobId" value="${returnJobId}">
                <input type="hidden" name="status" value="SHORTLISTED">
                <button class="button-primary" type="submit">Shortlist</button>
            </form>
        </c:if>
        <c:if test="${applicationView.canReject}">
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/mo/applications/status"
                  data-confirm="Mark this candidate as REJECTED for the current job?">
                <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                <input type="hidden" name="jobId" value="${returnJobId}">
                <input type="hidden" name="status" value="REJECTED">
                <button class="button-warning" type="submit">Reject</button>
            </form>
        </c:if>
        <c:if test="${applicationView.reviewLocked}">
            <span class="tag tag-muted">Review locked after TA withdrawal</span>
        </c:if>
        <a class="button-secondary" href="${backToListHref}">Back to Applications</a>
    </div>
</div>
<%@ include file="../common/footer.jspf" %>
