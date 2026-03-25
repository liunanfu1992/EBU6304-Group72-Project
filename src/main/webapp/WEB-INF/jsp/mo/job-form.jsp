<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>Create Job Listing</h2>
    <p class="muted">This form is already wired to the job service and repository so the MO module can expand on it directly.</p>

    <c:if test="${param.created eq '1'}">
        <div class="success">Job created successfully.</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/mo/jobs/new">
        <label for="title">Title</label>
        <input id="title" name="title" required>

        <label for="moduleCode">Module Code</label>
        <input id="moduleCode" name="moduleCode" required>

        <label for="description">Description</label>
        <textarea id="description" name="description" rows="5" required></textarea>

        <label for="weeklyHours">Weekly Hours</label>
        <input id="weeklyHours" name="weeklyHours" type="number" min="1" required>

        <label for="requiredSkills">Required Skills (comma separated)</label>
        <input id="requiredSkills" name="requiredSkills">

        <button type="submit">Create Job</button>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
