<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <div class="page-intro">
        <div>
            <span class="eyebrow">Access Portal</span>
            <h2 class="card-title">Login</h2>
            <p class="card-subtitle">Sign in with a TA, MO, or Admin account to access the recruitment workflow.</p>
        </div>
        <div class="metrics-row">
            <div class="metric-card">
                <span class="metric-value">3 roles</span>
                <span class="metric-label">TA, MO, and Admin access</span>
            </div>
        </div>
    </div>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    <c:if test="${param.registered eq '1'}">
        <div class="success">Registration completed. You can now sign in with your new account.</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-grid">
            <div class="field-card">
                <label for="username">Username</label>
                <input id="username" name="username" required>
            </div>

            <div class="field-card">
                <label for="password">Password</label>
                <input id="password" type="password" name="password" required>
                <div class="actions-row">
                    <button class="button-secondary" type="button" data-password-toggle="password">Show password</button>
                </div>
            </div>
        </div>

        <div class="actions-row">
            <button type="submit">Sign In</button>
            <a class="button-secondary" href="${pageContext.request.contextPath}/register">Create Account</a>
        </div>
    </form>
</div>

<%@ include file="../common/footer.jspf" %>
