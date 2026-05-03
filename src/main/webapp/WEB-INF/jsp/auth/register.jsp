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
                <input id="username" name="username" value="${formUsername}" required>
            </div>
            <div class="field-card">
                <label for="email">Email</label>
                <input id="email" name="email" type="email" value="${formEmail}" required>
            </div>
            <div class="field-card">
                <label for="password">Password</label>
                <input id="password" name="password" type="password" required>
            </div>
            <div class="field-card">
                <label for="role">Role</label>
                <select id="role" name="role" required>
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
