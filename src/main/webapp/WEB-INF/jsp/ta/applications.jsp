<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">TA Applications</span>
    <h2 class="card-title">My Applications</h2>
    <p class="card-subtitle">Track every TA application you have submitted and manage pending requests before they are reviewed.</p>

    <c:if test="${param.applied eq '1'}">
        <div class="success">Your application was submitted successfully and is now pending MO review.</div>
    </c:if>
    <c:if test="${param.withdrawn eq '1'}">
        <div class="success">The pending application was withdrawn successfully.</div>
    </c:if>
    <c:if test="${param.withdrawError eq 'status'}">
        <div class="error">Only pending applications can be withdrawn.</div>
    </c:if>
    <c:if test="${param.withdrawError eq 'missing'}">
        <div class="error">The application record could not be found under your account.</div>
    </c:if>
    <c:if test="${param.withdrawError eq '1'}">
        <div class="error">The application could not be withdrawn. Please try again.</div>
    </c:if>

    <c:choose>
        <c:when test="${empty applications}">
            <p>You have not submitted any application records yet.</p>
        </c:when>
        <c:otherwise>
            <div class="job-list">
                <c:forEach items="${applications}" var="applicationView">
                    <div class="job-card">
                        <div class="job-card-head">
                            <div>
                                <h3><c:out value="${applicationView.job == null ? 'Job unavailable' : applicationView.job.title}"/></h3>
                                <p class="muted">
                                    <c:out value="${applicationView.job == null ? '-' : applicationView.job.moduleCode}"/>
                                    |
                                    Submitted ${applicationView.submittedAtDisplay}
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

                        <c:if test="${applicationView.job != null}">
                            <p>${applicationView.job.description}</p>
                            <div class="job-section">
                                <strong>Required skills</strong>
                                <div class="tag-list">
                                    <c:forEach items="${applicationView.job.requiredSkills}" var="skill">
                                        <span class="tag">${skill}</span>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <div class="actions-row">
                            <c:if test="${applicationView.job != null}">
                                <a class="button-secondary" href="${pageContext.request.contextPath}/ta/jobs/view?jobId=${applicationView.job.id}">View Job</a>
                            </c:if>
                            <c:if test="${applicationView.withdrawable}">
                                <form class="inline-form" method="post"
                                      action="${pageContext.request.contextPath}/ta/applications/withdraw"
                                      data-confirm="Withdraw this pending application now?">
                                    <input type="hidden" name="applicationId" value="${applicationView.application.id}">
                                    <button class="button-warning" type="submit">Withdraw Application</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
