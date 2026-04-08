<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">MO Workspace</span>
    <h2 class="card-title">MO Dashboard</h2>
    <p class="card-subtitle">Post TA jobs, maintain active listings, and review candidate preview results for each opening.</p>
</div>

<div class="dashboard-grid">
    <a class="dashboard-link-card" href="${pageContext.request.contextPath}/mo/jobs/new">
        <strong>Post a New Job</strong>
        <span>Create a teaching assistant opening with structured and custom skill requirements.</span>
    </a>
    <a class="dashboard-link-card" href="${pageContext.request.contextPath}/mo/jobs">
        <strong>Manage My Jobs</strong>
        <span>Edit, close, reopen, and inspect candidate previews for jobs you own.</span>
    </a>
    <a class="dashboard-link-card" href="${pageContext.request.contextPath}/mo/applications">
        <strong>Review Applications</strong>
        <span>Open the Sprint 2 review workspace for applicants across your posted jobs.</span>
    </a>
</div>
<%@ include file="../common/footer.jspf" %>
