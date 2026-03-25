<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>TA Dashboard</h2>
    <p class="muted">Sprint 1 entry points for profile, CV, and job browsing.</p>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ta/profile">Manage Profile and Skills</a></li>
        <li><a href="${pageContext.request.contextPath}/ta/cv">CV Upload Module Hook</a></li>
        <li><a href="${pageContext.request.contextPath}/ta/jobs">Browse Available Jobs</a></li>
    </ul>
</div>
<%@ include file="../common/footer.jspf" %>
