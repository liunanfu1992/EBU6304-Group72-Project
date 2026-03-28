<%@ include file="../common/header.jspf" %>
<div class="card">
    <span class="eyebrow">Admin Console</span>
    <h2 class="card-title">Admin Dashboard</h2>
    <p class="card-subtitle">Sprint 1 admin work centers on local data storage, seeded accounts, record visibility, and secure read-only monitoring.</p>
</div>

<div class="metrics-row">
    <div class="metric-card">
        <span class="metric-value">${dashboard.userCount}</span>
        <span class="metric-label">Total accounts (${dashboard.taUserCount} TA / ${dashboard.moUserCount} MO / ${dashboard.adminUserCount} Admin)</span>
    </div>
    <div class="metric-card">
        <span class="metric-value">${dashboard.profileCount}</span>
        <span class="metric-label">Profiles saved (${dashboard.profilesWithCvCount} with CV)</span>
    </div>
    <div class="metric-card">
        <span class="metric-value">${dashboard.jobCount}</span>
        <span class="metric-label">Jobs stored (${dashboard.openJobCount} currently open)</span>
    </div>
    <div class="metric-card">
        <span class="metric-value">${dashboard.cvFileCount}</span>
        <span class="metric-label">CV files (${dashboard.totalCvSizeDisplay})</span>
    </div>
</div>

<div class="card">
    <h3 class="card-title">Runtime Storage Health</h3>
    <div class="dashboard-grid">
        <c:forEach items="${dashboard.pathStatuses}" var="pathStatus">
            <div class="metric-card">
                <strong>${pathStatus.label}</strong>
                <div class="tag-list">
                    <span class="${pathStatus.statusTagClass}">${pathStatus.statusLabel}</span>
                </div>
                <p class="metric-label">${pathStatus.detail}</p>
                <p class="helper">${pathStatus.path}</p>
            </div>
        </c:forEach>
    </div>
</div>

<div class="card">
    <h3 class="card-title">Seeded and Saved Accounts</h3>
    <c:choose>
        <c:when test="${empty dashboard.users}">
            <p class="muted">No user records are stored yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Email</th>
                    <th>User ID</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${dashboard.users}" var="user">
                    <tr>
                        <td>${user.username}</td>
                        <td>${user.role}</td>
                        <td>${user.email}</td>
                        <td><code>${user.id}</code></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <h3 class="card-title">TA Profile Records</h3>
    <c:choose>
        <c:when test="${empty dashboard.profiles}">
            <p class="muted">No profiles have been completed yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Student ID</th>
                    <th>Major</th>
                    <th>Email</th>
                    <th>Skills</th>
                    <th>CV</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${dashboard.profiles}" var="profile">
                    <tr>
                        <td><c:out value="${empty profile.name ? '-' : profile.name}"/></td>
                        <td><c:out value="${empty profile.studentId ? '-' : profile.studentId}"/></td>
                        <td><c:out value="${empty profile.major ? '-' : profile.major}"/></td>
                        <td><c:out value="${empty profile.email ? '-' : profile.email}"/></td>
                        <td>${profile.allSkills.size()}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty profile.cvPath}">
                                    <span class="tag">Uploaded</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag tag-muted">Missing</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <h3 class="card-title">Job Posting Records</h3>
    <c:choose>
        <c:when test="${empty dashboard.jobs}">
            <p class="muted">No jobs are stored yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Module</th>
                    <th>Status</th>
                    <th>Weekly Hours</th>
                    <th>Owner</th>
                    <th>Skills</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${dashboard.jobs}" var="job">
                    <tr>
                        <td>${job.title}</td>
                        <td>${job.moduleCode}</td>
                        <td>
                            <c:choose>
                                <c:when test="${job.open}">
                                    <span class="tag">OPEN</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag tag-muted">CLOSED</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${job.weeklyHours}</td>
                        <td><code>${job.moUserId}</code></td>
                        <td>${job.requiredSkills.size()}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <h3 class="card-title">Uploaded CV Files</h3>
    <p class="helper">This list is read-only and shows what currently exists in the controlled CV storage directory.</p>
    <c:choose>
        <c:when test="${empty dashboard.cvFiles}">
            <p class="muted">No uploaded CV files are currently stored.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>File Name</th>
                    <th>Owner</th>
                    <th>User ID</th>
                    <th>Size</th>
                    <th>Last Modified</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${dashboard.cvFiles}" var="cvFile">
                    <tr>
                        <td><code>${cvFile.fileName}</code></td>
                        <td>${cvFile.ownerDisplayName}</td>
                        <td><code>${cvFile.ownerUserId}</code></td>
                        <td>${cvFile.sizeDisplay}</td>
                        <td>${cvFile.lastModifiedDisplay}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../common/footer.jspf" %>
