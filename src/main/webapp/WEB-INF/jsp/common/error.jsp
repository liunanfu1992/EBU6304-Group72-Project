<%@ page isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jspf" %>
<div class="card">
    <h2>Something Went Wrong</h2>
    <p class="muted">The application hit an error while processing this request.</p>

    <c:if test="${not empty requestScope['javax.servlet.error.status_code']}">
        <p><strong>Status:</strong> ${requestScope["javax.servlet.error.status_code"]}</p>
    </c:if>

    <c:if test="${not empty requestScope['javax.servlet.error.request_uri']}">
        <p><strong>Request URI:</strong> ${requestScope["javax.servlet.error.request_uri"]}</p>
    </c:if>

    <c:if test="${not empty requestScope['javax.servlet.error.message']}">
        <p><strong>Message:</strong> ${requestScope["javax.servlet.error.message"]}</p>
    </c:if>

    <p><a href="${pageContext.request.contextPath}/home">Return Home</a></p>
</div>
<%@ include file="footer.jspf" %>
