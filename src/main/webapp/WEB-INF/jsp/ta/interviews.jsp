<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">TA Interviews</span>
    <h2 class="card-title">My Interview Schedule</h2>
    <p class="card-subtitle">Review interview arrangements linked to shortlisted applications and confirm attendance.</p>

    <div class="workflow-strip">
        <div class="workflow-step">
            <strong>1. Review schedule</strong>
            <span>Check time, location, and meeting link from the module owner.</span>
        </div>
        <div class="workflow-step">
            <strong>2. Confirm attendance</strong>
            <span>Confirm shortlisted interview invitations before the session.</span>
        </div>
        <div class="workflow-step">
            <strong>3. Await outcome</strong>
            <span>Final offers or rejections appear in your Applications page.</span>
        </div>
    </div>

    <c:if test="${param.confirmed eq '1'}">
        <div class="success">Your interview attendance was confirmed.</div>
    </c:if>
    <c:choose>
        <c:when test="${param.confirmError eq 'missing'}">
            <div class="error">The interview record could not be found under your account.</div>
        </c:when>
        <c:when test="${param.confirmError eq 'unscheduled'}">
            <div class="error">This application does not have a scheduled interview yet.</div>
        </c:when>
        <c:when test="${param.confirmError eq 'status'}">
            <div class="error">Only shortlisted interview invitations can be confirmed.</div>
        </c:when>
        <c:when test="${not empty param.confirmError}">
            <div class="error">The interview confirmation could not be saved. Please try again.</div>
        </c:when>
    </c:choose>

    <div class="summary-grid">
        <div class="summary-card">
            <strong>${interviewTotal}</strong>
            <span>Scheduled Interviews</span>
        </div>
        <div class="summary-card">
            <strong>${interviewPendingCount}</strong>
            <span>Need Confirmation</span>
        </div>
        <div class="summary-card">
            <strong>${interviewConfirmedCount}</strong>
            <span>Confirmed</span>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty interviews}">
            <div class="empty-state">
                <p>No interviews have been scheduled for your applications yet.</p>
                <div class="actions-row">
                    <a class="button-primary" href="${pageContext.request.contextPath}/ta/applications">View Applications</a>
                    <a class="button-secondary" href="${pageContext.request.contextPath}/ta/jobs">Browse Jobs</a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="job-list">
                <c:forEach items="${interviews}" var="applicationView">
                    <div class="job-card">
                        <div class="job-card-head">
                            <div>
                                <h3><c:out value="${applicationView.jobTitleDisplay}"/></h3>
                                <p class="muted">
                                    <c:out value="${applicationView.moduleCodeDisplay}"/>
                                    |
                                    ${applicationView.interviewStartDisplay}
                                </p>
                                <p class="helper">
                                    Module Owner: ${applicationView.moduleOwnerDisplayName}
                                    <c:if test="${not empty applicationView.moduleOwnerEmail}">
                                        | ${applicationView.moduleOwnerEmail}
                                    </c:if>
                                </p>
                            </div>
                            <span class="${applicationView.statusTagClass}">${applicationView.statusLabel}</span>
                        </div>

                        <c:if test="${applicationView.attendanceConfirmable}">
                            <div class="warning">This interview invitation is waiting for your attendance confirmation.</div>
                        </c:if>

                        <div class="detail-grid">
                            <div class="detail-panel">
                                <h4>Interview Arrangement</h4>
                                <p><strong>Time:</strong> ${applicationView.interviewStartDisplay}</p>
                                <p><strong>Location:</strong> ${applicationView.interviewLocationDisplay}</p>
                                <c:if test="${applicationView.hasInterviewLink}">
                                    <a class="button-secondary" href="<c:out value="${applicationView.interviewLink}"/>"
                                       target="_blank" rel="noopener">Open Meeting Link</a>
                                </c:if>
                            </div>
                            <div class="detail-panel">
                                <h4>Attendance</h4>
                                <div class="tag-list">
                                    <span class="${applicationView.attendanceTagClass}">${applicationView.attendanceLabel}</span>
                                </div>
                                <c:if test="${applicationView.attendanceConfirmable}">
                                    <form class="inline-form" method="post"
                                          action="${pageContext.request.contextPath}/ta/interviews/confirm"
                                          data-confirm="Confirm that you will attend this interview?">
                                        <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                                        <button class="button-primary" type="submit">Confirm Attendance</button>
                                    </form>
                                </c:if>
                                <c:if test="${applicationView.attendanceConfirmed}">
                                    <div class="result-panel">
                                        <strong>Attendance confirmed</strong>
                                        <span>Your confirmation has been saved for this interview schedule.</span>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
