<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <h2>CV Upload</h2>
    <p class="muted">Upload or replace your CV with basic file validation and secure storage rules.</p>

    <c:if test="${param.uploaded eq '1'}">
        <div class="success">CV uploaded successfully.</div>
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

    <div class="info">
        <strong>Upload rules:</strong>
        Allowed types: ${allowedCvTypes}. Maximum size: ${maxCvSizeMb}MB.
    </div>

    <c:choose>
        <c:when test="${not empty profile.cvPath}">
            <p><strong>Current uploaded CV:</strong> ${profile.cvFileName}</p>
            <p class="helper">Uploading a new file will replace the current CV reference in your profile.</p>
        </c:when>
        <c:otherwise>
            <p class="muted">No CV has been uploaded yet.</p>
        </c:otherwise>
    </c:choose>

    <form method="post" action="${pageContext.request.contextPath}/ta/cv" enctype="multipart/form-data">
        <label for="cvFile">Choose CV File</label>
        <input id="cvFile" name="cvFile" type="file" accept=".pdf,.doc,.docx" required>

        <button type="submit">Upload CV</button>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
