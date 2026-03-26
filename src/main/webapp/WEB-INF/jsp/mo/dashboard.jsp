<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>MO Dashboard</h2>
    <p class="muted">Sprint 1 entry point for MO job creation, editing, status management, and candidate preview.</p>
    <ul>
        <li><a href="${pageContext.request.contextPath}/mo/jobs/new">Post a New Job</a></li>
        <li><a href="${pageContext.request.contextPath}/mo/jobs">Manage My Jobs</a></li>
    </ul>
</div>
<%@ include file="../common/footer.jspf" %>
