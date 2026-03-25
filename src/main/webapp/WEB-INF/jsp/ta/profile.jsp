<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>TA Profile</h2>
    <p class="muted">This page is ready for the TA profile and skills module to evolve.</p>

    <c:if test="${param.saved eq '1'}">
        <div class="success">Profile saved successfully.</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/ta/profile">
        <label for="name">Name</label>
        <input id="name" name="name" value="${profile.name}" required>

        <label for="studentId">Student ID</label>
        <input id="studentId" name="studentId" value="${profile.studentId}" required>

        <label for="major">Major</label>
        <input id="major" name="major" value="${profile.major}" required>

        <label for="skills">Skills (comma separated)</label>
        <input id="skills" name="skills" value="${profile.skills}">

        <button type="submit">Save Profile</button>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
