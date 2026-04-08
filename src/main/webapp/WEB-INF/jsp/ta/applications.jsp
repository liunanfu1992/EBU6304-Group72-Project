<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">TA Applications</span>
    <h2 class="card-title">My Applications</h2>
    <p class="card-subtitle">This shared Sprint 2 workspace shows the application records connected to your TA account.</p>

    <div class="info">Jiayang's backbone provides the shared application timeline here. Apply and withdraw actions will attach to this page in the next Sprint 2 task.</div>

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
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
