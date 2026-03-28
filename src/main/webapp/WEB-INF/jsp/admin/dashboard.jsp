<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Admin Console</span>
    <h2 class="card-title">Admin Dashboard</h2>
    <p class="card-subtitle">Sprint 1 admin work centers on local data storage, seeded accounts, and secure record foundations.</p>
</div>

<div class="metrics-row">
    <div class="metric-card">
        <span class="metric-value">App Home</span>
        <span class="metric-label">${appHome}</span>
    </div>
    <div class="metric-card">
        <span class="metric-value">Current Scope</span>
        <span class="metric-label">JSON persistence, account seeding, and access control scaffolding</span>
    </div>
</div>

<div class="card">
    <h3 class="card-title">Extension Points</h3>
    <div class="dashboard-grid">
        <div class="metric-card">
            <strong>Data Records</strong>
            <span class="metric-label">JSON file structures, storage directories, and runtime app home layout.</span>
        </div>
        <div class="metric-card">
            <strong>Security</strong>
            <span class="metric-label">Password hashing, user role handling, and controlled file access.</span>
        </div>
        <div class="metric-card">
            <strong>Future Monitoring</strong>
            <span class="metric-label">Administrative insights, workload views, and secure record operations.</span>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jspf" %>
