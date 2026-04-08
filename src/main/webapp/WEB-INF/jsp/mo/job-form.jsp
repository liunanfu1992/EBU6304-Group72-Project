<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Job Editor</span>
    <h2 class="card-title">${pageTitle}</h2>
    <p class="card-subtitle">${pageDescription}</p>

    <c:if test="${param.created eq '1'}">
        <div class="success">Job created successfully.</div>
    </c:if>

    <c:if test="${param.draftSaved eq '1'}">
        <div class="success">Draft saved successfully. This job remains hidden from TA users until you publish it.</div>
    </c:if>

    <c:if test="${not empty jobDraft and jobDraft.draft}">
        <div class="info">Draft jobs can be saved with incomplete fields in Sprint 1, but publishing still requires all required fields.</div>
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

        <div class="form-section">
            <div class="section-label">
                <label>Job Basics</label>
                <span class="section-note">Capture the core listing information for this opening.</span>
            </div>
            <div class="form-grid">
                <div class="field-card">
                    <label for="title">Title</label>
                    <input id="title" name="title" value="${jobDraft.title}" required>
                </div>
                <div class="field-card">
                    <label for="moduleCode">Module Code</label>
                    <input id="moduleCode" name="moduleCode" value="${jobDraft.moduleCode}" required>
                </div>
                <div class="field-card">
                    <label for="weeklyHours">Weekly Hours</label>
                    <input id="weeklyHours" name="weeklyHours" type="number" min="1" value="${jobDraft.weeklyHours}" required>
                </div>
                <div class="field-card full-span">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="5" required>${jobDraft.description}</textarea>
                </div>
            </div>
        </div>

        <div class="form-section">
            <div class="section-label">
                <label>Predefined Required Skills</label>
                <span id="mo-skill-count" class="selected-counter">0 selected</span>
            </div>
            <div class="skill-grid" data-skill-count="mo-skill-count">
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
                <label for="customSkills">Custom Required Skills (comma separated)</label>
                <input id="customSkills" name="customSkills" value="${jobDraft.customRequiredSkillsInput}">
            </div>
        </div>

        <p class="helper">Predefined skills will be used directly by the TA-side matching preview. Custom skills are displayed, but not scored in Sprint 1.</p>

        <div class="actions-row">
            <button class="button-primary" type="submit" name="submitAction" value="${primaryActionValue}">${primarySubmitLabel}</button>
            <button class="button-secondary" type="submit" name="submitAction" value="saveDraft" formnovalidate>Save as Draft</button>
            <a class="button-secondary" href="${cancelPath}">Cancel</a>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
