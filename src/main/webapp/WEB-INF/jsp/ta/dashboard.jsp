<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">TA Workspace</span>
    <h2 class="card-title">TA Dashboard</h2>
    <p class="card-subtitle">Complete your profile, upload your CV, and review matching jobs from one place.</p>
</div>

<div class="dashboard-grid">
    <a class="dashboard-link-card" href="${pageContext.request.contextPath}/ta/profile">
        <strong>Manage Profile and Skills</strong>
        <span>Maintain your identity details and structured skill set for matching.</span>
    </a>
    <a class="dashboard-link-card" href="${pageContext.request.contextPath}/ta/cv">
        <strong>Upload or Replace CV</strong>
        <span>Keep a validated CV on file and download the current copy when needed.</span>
    </a>
    <a class="dashboard-link-card" href="${pageContext.request.contextPath}/ta/jobs">
        <strong>Browse Available Jobs</strong>
        <span>See the current TA openings and your predefined-skill fit at a glance.</span>
    </a>
</div>
<%@ include file="../common/footer.jspf" %>
