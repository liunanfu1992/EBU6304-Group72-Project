<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>TA Profile</h2>
    <p class="muted">Complete your profile with a structured skill record for future job matching.</p>
    <p class="helper">Matching in Sprint 1 is based on predefined skills first. Custom skills are kept as supplementary context.</p>

    <c:if test="${param.saved eq '1'}">
        <div class="success">Profile saved successfully.</div>
    </c:if>

    <c:if test="${not empty errors}">
        <div class="error">
            <strong>Please fix the following:</strong>
            <ul>
                <c:forEach items="${errors}" var="error">
                    <li>${error}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/ta/profile">
        <label for="name">Name</label>
        <input id="name" name="name" value="${profile.name}" required>

        <label for="studentId">Student ID</label>
        <input id="studentId" name="studentId" value="${profile.studentId}" required>

        <label for="major">Major</label>
        <input id="major" name="major" value="${profile.major}" required>

        <label for="email">Email</label>
        <input id="email" name="email" type="email" value="${profile.email}" required>

        <label>Predefined Skills</label>
        <div class="skill-grid">
            <c:forEach items="${availableSkills}" var="skill">
                <label class="skill-option">
                    <input type="checkbox" name="selectedSkills" value="${skill}"
                        <c:if test="${selectedSkillMap[skill]}">checked</c:if>>
                    <span>${skill}</span>
                </label>
            </c:forEach>
        </div>

        <label for="customSkills">Custom Skills (comma separated)</label>
        <input id="customSkills" name="customSkills" value="${profile.customSkillsInput}">

        <c:if test="${not empty profile.allSkills}">
            <label>Current Skill Tags</label>
            <div class="tag-list">
                <c:forEach items="${profile.allSkills}" var="skill">
                    <span class="tag">${skill}</span>
                </c:forEach>
            </div>
        </c:if>

        <c:if test="${empty profile.selectedSkills}">
            <div class="info">
                Select at least one predefined skill so the job page can show a meaningful match result.
            </div>
        </c:if>

        <button type="submit">Save Profile</button>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
