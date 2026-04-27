<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">TA Interviews</span>
    <h2 class="card-title">My Interview Schedule</h2>
    <p class="card-subtitle">Review interview arrangements linked to shortlisted applications and confirm attendance.</p>

    <c:if test="${param.confirmed eq '1'}">
        <div class="success">Your interview attendance was confirmed.</div>
    </c:if>
    <c:if test="${not empty param.confirmError}">
        <div class="error">The interview confirmation could not be saved. ${param.confirmError}</div>
    </c:if>

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
                                <h3><c:out value="${applicationView.job == null ? 'Job unavailable' : applicationView.job.title}"/></h3>
                                <p class="muted">
                                    <c:out value="${applicationView.job == null ? '-' : applicationView.job.moduleCode}"/>
                                    |
                                    ${applicationView.interviewStartDisplay}
                                </p>
                                <p class="helper">Module Owner: ${applicationView.moduleOwnerDisplayName}</p>
                            </div>
                            <span class="${applicationView.statusTagClass}">${applicationView.statusLabel}</span>
                        </div>

                        <div class="detail-grid">
                            <div class="detail-panel">
                                <h4>Interview Location</h4>
                                <p>${applicationView.interviewLocationDisplay}</p>
                                <c:if test="${not empty applicationView.interviewLink}">
                                    <a class="button-secondary" href="${applicationView.interviewLink}" target="_blank" rel="noopener">Open Meeting Link</a>
                                </c:if>
                            </div>
                            <div class="detail-panel">
                                <h4>Attendance</h4>
                                <p>${applicationView.attendanceLabel}</p>
                                <c:if test="${applicationView.attendanceConfirmable}">
                                    <form class="inline-form" method="post"
                                          action="${pageContext.request.contextPath}/ta/interviews/confirm"
                                          data-confirm="Confirm that you will attend this interview?">
                                        <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                                        <button class="button-primary" type="submit">Confirm Attendance</button>
                                    </form>
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
