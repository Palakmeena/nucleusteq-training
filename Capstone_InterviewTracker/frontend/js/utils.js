// ============================================================
// utils.js — shared helpers used across all dashboard pages
// ============================================================

// TOAST NOTIFICATION
function showToast(message, type = 'default') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `toast toast-${type || 'default'}`;
    toast.innerHTML = `<span>${type==='success'?'':type==='error'?'':''}</span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => { toast.classList.add('toast-exit'); setTimeout(()=>toast.remove(),300); }, 3000);
}

// HELPERS
function getBasePath() {
    const path = window.location.pathname;
    if (path.includes('/pages/')) {
        return '../../';
    }
    return '';
}

// SIDEBAR HTML — builds role-based sidebar
function buildSidebar(role, activePage) {
    const base = getBasePath();
    const navMap = {
        HR: [
            { key: 'overview',   icon: svgDashboard, label: 'Overview',          href: base + 'pages/hr/overview.html' },
            { key: 'jobs',       icon: svgBriefcase, label: 'Job Descriptions',  href: base + 'pages/hr/job-management.html' },
            { key: 'candidates', icon: svgUsers,     label: 'Candidates',        href: base + 'pages/hr/candidates.html' },
            { key: 'panels',     icon: svgUserCheck, label: 'Panel Members',     href: base + 'pages/hr/panel-onboarding.html' },
            { key: 'interviews', icon: svgFileText,  label: 'Feedback',          href: base + 'pages/hr/panel-feedback.html' },
        ],
        CANDIDATE: [
            { key: 'dashboard',  icon: svgDashboard, label: 'My Progress',       href: base + 'pages/candidate/dashboard.html' },
            { key: 'jobs',       icon: svgBriefcase, label: 'Jobs',              href: base + 'pages/candidate/jobs.html' },
        ],
        PANEL: [
            { key: 'overview',   icon: svgDashboard, label: 'Overview',          href: base + 'pages/panel/overview.html' },
            { key: 'interviews', icon: svgCalendar,  label: 'Assigned Interviews',href: base + 'pages/panel/assigned-Interviews.html' },
            { key: 'feedback',   icon: svgFileText,  label: 'Feedback',          href: base + 'pages/panel/feedback.html' },
        ]
    };

    const items = navMap[role] || [];
    const fullName = auth.getFullName() || 'User';
    const initials = fullName.split(' ').map(n=>n[0]).join('').toUpperCase().slice(0,2);
    const roleLabel = { HR: 'Hiring Manager', CANDIDATE: 'Candidate', PANEL: 'Panel Member' }[role] || role;

    return `
        <div class="brand">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="m12 15-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"/>
                <path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0"/>
            </svg>
            HireTrack
        </div>
        <div class="profile-section">
            <div class="profile-title">${fullName}</div>
            <div class="profile-role">${roleLabel}</div>
        </div>
        <nav class="nav-menu">
            ${items.map(item => `
                <a href="${item.href}" class="nav-item ${activePage === item.key ? 'active' : ''}">
                    ${item.icon} ${item.label}
                </a>
            `).join('')}
        </nav>
        <div class="sidebar-bottom">
            <a href="${base}index.html" class="bottom-item">${svgHome} Home</a>
            <a href="#" class="bottom-item logout" onclick="auth.logout(); return false;">${svgLogout} Sign Out</a>
            <div class="sidebar-user-meta">
                <div class="sidebar-avatar">${initials}</div>
                <div class="overflow-hidden">
                    <div class="sidebar-user-name">${fullName}</div>
                    <div class="sidebar-user-email">${auth.getEmail()||''}</div>
                </div>
            </div>
        </div>
    `;
}

// STAGE BADGE
function stageBadge(stage) {
    const map = {
        PROFILING:    { cls: 'badge-gray',   label: 'Profiling' },
        SCREENING:    { cls: 'badge-amber',  label: 'Screening' },
        L1_TECHNICAL: { cls: 'badge-blue',   label: 'L1 Technical' },
        L2_TECHNICAL: { cls: 'badge-purple', label: 'L2 Technical' },
        HR_ROUND:     { cls: 'badge-amber',  label: 'HR Round' },
        SELECTED:     { cls: 'badge-green',  label: 'Selected' },
        REJECTED:     { cls: 'badge-red',    label: 'Rejected' },
    };
    const s = map[stage] || { cls: 'badge-gray', label: stage };
    return `<span class="jd-badge ${s.cls} badge-capitalize">${s.label}</span>`;
}

// FORMAT DATE
function formatDate(str) {
    if (!str) return '—';
    return new Date(str).toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' });
}

// SVG ICONS — defined in svg-icons.js
// Import: Add <script src="../js/svg-icons.js"></script> BEFORE utils.js in your HTML files


// MODAL HELPERS
function showModal(title, contentHtml, footerHtml = '', modalClass = '') {
    // Remove existing modal if any
    const existing = document.querySelector('.modal-overlay');
    if (existing) existing.remove();

    const modal = document.createElement('div');
    modal.className = 'modal-overlay active';
    modal.innerHTML = `
        <div class="modal-content ${modalClass}">
            <div class="modal-header">
                <h3 class="modal-title-compact">${title}</h3>
                <button class="close-modal modal-close-circle" onclick="this.closest('.modal-overlay').remove()">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                </button>
            </div>
            <div class="modal-body">
                ${contentHtml}
            </div>
            ${footerHtml ? `<div class="modal-footer modal-footer-actions">${footerHtml}</div>` : ''}
        </div>
    `;
    document.body.appendChild(modal);
}

function showResumeModal(resumeUrl) {
    if (!resumeUrl) {
        showToast('No resume available for this candidate', 'error');
        return;
    }
    // Open resume in a new tab (same as 'View Full') instead of showing popup/modal
    try {
        window.open(resumeUrl, '_blank');
    } catch (e) {
        // Fallback: navigate in same tab
        window.location.href = resumeUrl;
    }
}

function showJdModal(jdData) {
    const typeLabels = { FULL_TIME: 'Full Time', CONTRACT: 'Contract', REMOTE: 'Remote' };
    const jobType = typeLabels[jdData.jobType] || jdData.jobType || 'N/A';
    const skillsList = (jdData.skills || []).length > 0 
        ? jdData.skills.map(s => `<span class="jd-modal-skill">${s}</span>`).join('')
        : '<span class="jd-modal-empty">No skills specified</span>';
    
    const content = `
        <div class="jd-modal-body">
            <div class="jd-modal-section">
                <label class="jd-modal-label">Job Title</label>
                <div class="jd-modal-title">${jdData.title || jdData.jobTitle || 'N/A'}</div>
            </div>
            <div class="jd-modal-grid">
                <div class="jd-modal-section">
                    <label class="jd-modal-label">Location</label>
                    <div class="jd-modal-text">${jdData.location || 'N/A'}</div>
                </div>
                <div class="jd-modal-section">
                    <label class="jd-modal-label">Job Type</label>
                    <div class="jd-modal-text">${jobType}</div>
                </div>
            </div>
            <div class="jd-modal-grid">
                <div class="jd-modal-section">
                    <label class="jd-modal-label">Experience (Years)</label>
                    <div class="jd-modal-text">${jdData.minExperience || '0'} - ${jdData.maxExperience || 'N/A'}</div>
                </div>
                <div class="jd-modal-section">
                    <label class="jd-modal-label">Salary (LPA)</label>
                    <div class="jd-modal-text">${jdData.minSalary || '0'} - ${jdData.maxSalary || 'N/A'}</div>
                </div>
            </div>
            <div class="jd-modal-section">
                <label class="jd-modal-label">Required Skills</label>
                <div class="jd-modal-skills">${skillsList}</div>
            </div>
            <div class="jd-modal-section">
                <label class="jd-modal-label">Job Description</label>
                <div class="jd-modal-text jd-modal-desc">${jdData.details || jdData.jobDescription || 'No details provided.'}</div>
            </div>
        </div>
    `;
    showModal('Job Details', content, '', 'job-details-modal');
}

// Make available globally
window.showToast = showToast;
window.buildSidebar = buildSidebar;
window.stageBadge = stageBadge;
window.formatDate = formatDate;
window.getBasePath = getBasePath;
window.showResumeModal = showResumeModal;
window.showJdModal = showJdModal;
