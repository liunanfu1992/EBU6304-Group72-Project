<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>${pageTitle}</h2>
    <p class="muted">${pageDescription}</p>

    <c:if test="${param.created eq '1'}">
        <div class="success">Job created successfully.</div>
    </c:if>

    <c:if test="${param.updated eq '1'}">
        <div class="success">Job updated successfully.</div>
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

    <form method="post" action="${formAction}">
        <c:if test="${not empty jobDraft.id}">
            <input type="hidden" name="jobId" value="${jobDraft.id}">
        </c:if>

        <label for="title">Title</label>
        <input id="title" name="title" value="${jobDraft.title}" required>

        <label for="moduleCode">Module Code</label>
        <input id="moduleCode" name="moduleCode" value="${jobDraft.moduleCode}" required>

        <label for="description">Description</label>
        <textarea id="description" name="description" rows="5" required>${jobDraft.description}</textarea>

        <label for="weeklyHours">Weekly Hours</label>
        <input id="weeklyHours" name="weeklyHours" type="number" min="1" value="${jobDraft.weeklyHours}" required>

        <label>Predefined Required Skills</label>
        <div class="skill-grid">
            <c:forEach items="${availableSkills}" var="skill">
                <label class="skill-option">
                    <input type="checkbox" name="selectedSkills" value="${skill}"
                        <c:if test="${selectedSkillMap[skill]}">checked</c:if>>
                    <span>${skill}</span>
                </label>
            </c:forEach>
        </div>

        <label for="customSkills">Custom Required Skills (comma separated)</label>
        <input id="customSkills" name="customSkills" value="${jobDraft.customRequiredSkillsInput}">

        <p class="helper">Predefined skills will be used directly by the TA-side matching preview. Custom skills are displayed, but not scored in Sprint 1.</p>

        <div class="actions-row">
            <button type="submit">${submitLabel}</button>
            <a class="button-secondary" href="${cancelPath}">Cancel</a>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
