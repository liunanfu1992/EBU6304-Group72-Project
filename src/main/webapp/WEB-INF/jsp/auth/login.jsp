<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>Login</h2>
    <p class="muted">Use the seeded accounts to enter the scaffolded system.</p>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="username">Username</label>
        <input id="username" name="username" required>

        <label for="password">Password</label>
        <input id="password" type="password" name="password" required>

        <button type="submit">Sign In</button>
    </form>
</div>

<div class="card">
    <h3>Seeded Demo Accounts</h3>
    <p><strong>TA:</strong> <code>ta-demo</code> / <code>password123</code></p>
    <p><strong>MO:</strong> <code>mo-demo</code> / <code>password123</code></p>
    <p><strong>Admin:</strong> <code>admin-demo</code> / <code>password123</code></p>
</div>
<%@ include file="../common/footer.jspf" %>
