<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">TA Profile</span>
    <h2 class="card-title">Profile and Skills</h2>
    <p class="card-subtitle">Complete your profile with a structured skill record for future job matching.</p>
    <p class="helper">Matching is based on predefined skills first. Custom skills are kept as supplementary context.</p>

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
        <div class="form-section">
            <div class="section-label">
                <label>Basic Information</label>
                <span class="section-note">These fields are used across TA-side profile and candidate preview pages.</span>
            </div>
            <div class="form-grid">
                <div class="field-card">
                    <label for="name">Name</label>
                    <input id="name" name="name" value="${profile.name}" required>
                </div>
                <div class="field-card">
                    <label for="studentId">Student ID</label>
                    <input id="studentId" name="studentId" value="${profile.studentId}" required>
                </div>
                <div class="field-card">
                    <label for="major">Major</label>
                    <input id="major" name="major" value="${profile.major}" required>
                </div>
                <div class="field-card">
                    <label for="email">Email</label>
                    <input id="email" name="email" type="email" value="${profile.email}" required>
                </div>
            </div>
        </div>

        <div class="form-section">
            <div class="section-label">
                <label>Predefined Skills</label>
                <span id="ta-skill-count" class="selected-counter">0 selected</span>
            </div>
            <div class="skill-grid" data-skill-count="ta-skill-count">
                <c:forEach items="${availableSkills}" var="skill">
                    <label class="skill-option">
                        <input type="checkbox" name="selectedSkills" value="${skill}"
                            <c:if test="${selectedSkillMap[skill]}">checked</c:if>>
                        <span>${skill}</span>
                    </label>
                </c:forEach>
            </div>
        </div>

        <div class="form-section">
            <div class="field-card full-span">
                <label for="customSkills">Custom Skills (comma separated)</label>
                <input id="customSkills" name="customSkills" value="${profile.customSkillsInput}">
            </div>
        </div>

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

        <div class="actions-row">
            <button type="submit">Save Profile</button>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
