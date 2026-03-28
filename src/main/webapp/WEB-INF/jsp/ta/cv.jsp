<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">CV Center</span>
    <h2 class="card-title">CV Upload</h2>
    <p class="card-subtitle">Upload or replace your CV with basic file validation and secure storage rules.</p>

    <c:if test="${param.uploaded eq '1'}">
        <div class="success">CV uploaded successfully.</div>
    </c:if>

    <c:if test="${param.deleted eq '1'}">
        <div class="success">Current CV deleted successfully.</div>
    </c:if>

    <c:if test="${param.notFound eq '1'}">
        <div class="info">No current CV was found to delete.</div>
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
            <div class="actions-row">
                <a class="button-secondary" href="${pageContext.request.contextPath}/ta/cv/download">Download Current CV</a>
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/ta/cv" data-confirm="Delete your current CV from the system?">
                    <input type="hidden" name="action" value="delete">
                    <button class="button-warning" type="submit">Delete Current CV</button>
                </form>
            </div>
            <p class="helper">The system currently supports secure upload and download. In-system preview can be added later.</p>
        </c:when>
        <c:otherwise>
            <p class="muted">No CV has been uploaded yet.</p>
        </c:otherwise>
    </c:choose>

    <form method="post" action="${pageContext.request.contextPath}/ta/cv" enctype="multipart/form-data">
        <label for="cvFile">Choose CV File</label>
        <input id="cvFile" name="cvFile" type="file" accept=".pdf,.doc,.docx" required data-file-input data-file-target="cv-file-meta">
        <div id="cv-file-meta" class="file-meta">No file selected yet.</div>

        <div class="actions-row">
            <button type="submit">Upload CV</button>
        </div>
    </form>
</div>
<%@ include file="../common/footer.jspf" %>
