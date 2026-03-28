<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <div class="page-intro">
        <div>
            <span class="eyebrow">Access Portal</span>
            <h2 class="card-title">Login</h2>
            <p class="card-subtitle">Use a seeded demo account to enter the Sprint 1 workflow and test role-based pages.</p>
        </div>
        <div class="metrics-row">
            <div class="metric-card">
                <span class="metric-value">3 roles</span>
                <span class="metric-label">TA, MO, and Admin demo entries</span>
            </div>
        </div>
    </div>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
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
        </div>
    </form>
</div>

<div class="card">
    <span class="eyebrow">Demo Accounts</span>
    <h3 class="card-title">Seeded Demo Accounts</h3>
    <div class="dashboard-grid">
        <div class="metric-card">
            <strong>TA</strong>
            <span class="metric-label"><code>ta-demo</code> / <code>password123</code></span>
        </div>
        <div class="metric-card">
            <strong>MO</strong>
            <span class="metric-label"><code>mo-demo</code> / <code>password123</code></span>
        </div>
        <div class="metric-card">
            <strong>Admin</strong>
            <span class="metric-label"><code>admin-demo</code> / <code>password123</code></span>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jspf" %>
