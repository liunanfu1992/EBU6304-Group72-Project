<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <div class="page-intro">
        <div>
            <span class="eyebrow">Account Setup</span>
            <h2 class="card-title">Register</h2>
            <p class="card-subtitle">Create a TA or MO account to join the recruitment workflow.</p>
        </div>
    </div>

    <c:if test="${not empty errors}">
        <div class="error">
            <c:forEach items="${errors}" var="error">
                <div>${error}</div>
            </c:forEach>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <div class="form-grid">
            <div class="field-card">
                <label for="username">Username</label>
                <input id="username" name="username" value="${formUsername}" required minlength="3" maxlength="30"
                       pattern="[A-Za-z0-9._-]{3,30}"
                       data-validation-message="Username must be 3-30 characters and use only letters, numbers, dots, underscores, or hyphens.">
                <p class="helper">Use 3-30 letters, numbers, dots, underscores, or hyphens.</p>
            </div>
            <div class="field-card">
                <label for="email">Email</label>
                <input id="email" name="email" type="email" value="${formEmail}" required
                       data-validation-message="Email must be a valid address.">
            </div>
            <div class="field-card">
                <label for="password">Password</label>
                <input id="password" name="password" type="password" required minlength="8"
                       data-validation-message="Password must be at least 8 characters.">
                <p class="helper">Use at least 8 characters.</p>
            </div>
            <div class="field-card">
                <label for="confirmPassword">Confirm Password</label>
                <input id="confirmPassword" name="confirmPassword" type="password" required minlength="8"
                       data-validation-message="Password confirmation is required.">
            </div>
            <div class="field-card">
                <label for="role">Role</label>
                <select id="role" name="role" required data-validation-message="Please select TA or MO.">
                    <option value="">Select role</option>
                    <c:forEach items="${roleOptions}" var="role">
                        <option value="${role}" <c:if test="${selectedRole eq role}">selected</c:if>>${role}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="actions-row">
            <button type="submit">Create Account</button>
            <a class="button-secondary" href="${pageContext.request.contextPath}/login">Back to Login</a>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
