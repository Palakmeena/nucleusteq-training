// ============================================================
// utils.js — shared helpers used across all dashboard pages
// ============================================================

// TOAST NOTIFICATION
function showToast(message, type = 'default') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        container.style.cssText = 'position:fixed;bottom:24px;right:24px;z-index:9999;display:flex;flex-direction:column;gap:10px;';
        document.body.appendChild(container);
    }
    const colors = { success: '#10B981', error: '#EF4444', default: '#1e293b' };
    const toast = document.createElement('div');
    toast.style.cssText = `background:${colors[type]||colors.default};color:white;padding:12px 18px;border-radius:8px;font-size:14px;display:flex;align-items:center;gap:10px;box-shadow:0 4px 16px rgba(0,0,0,0.15);min-width:260px;animation:slideIn 0.3s ease;`;
    toast.innerHTML = `<span>${type==='success'?'':type==='error'?'':''}</span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => { toast.style.opacity='0'; toast.style.transform='translateX(100px)'; toast.style.transition='0.3s'; setTimeout(()=>toast.remove(),300); }, 3000);
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
            { key: 'jobs',       icon: svgBriefcase, label: 'Job Descriptions',  href: base + 'pages/hr/jobs.html' },
            { key: 'candidates', icon: svgUsers,     label: 'Candidates',        href: base + 'pages/hr/candidates.html' },
            { key: 'panels',     icon: svgUserCheck, label: 'Panel Members',     href: base + 'pages/hr/panel-onboarding.html' },
            { key: 'interviews', icon: svgFileText,  label: 'Feedback',          href: base + 'pages/hr/schedule-interview.html' },
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
            <div style="display:flex;align-items:center;gap:10px;padding:10px 12px;margin-top:8px;border-top:1px solid #e2e8f0;">
                <div style="width:32px;height:32px;background:#eef2ff;border-radius:50%;display:flex;align-items:center;justify-content:center;font-weight:700;color:#4f46e5;font-size:13px;flex-shrink:0;">${initials}</div>
                <div style="overflow:hidden;">
                    <div style="font-size:13px;font-weight:600;color:#0f172a;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${fullName}</div>
                    <div style="font-size:11px;color:#64748b;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${auth.getEmail()||''}</div>
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
    return `<span class="jd-badge ${s.cls}" style="text-transform:none;">${s.label}</span>`;
}

// FORMAT DATE
function formatDate(str) {
    if (!str) return '—';
    return new Date(str).toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' });
}

// SVG ICONS — defined in svg-icons.js
// Import: Add <script src="../js/svg-icons.js"></script> BEFORE utils.js in your HTML files


// MODAL HELPERS
function showModal(title, contentHtml, footerHtml = '') {
    // Remove existing modal if any
    const existing = document.querySelector('.modal-overlay');
    if (existing) existing.remove();

    const modal = document.createElement('div');
    modal.className = 'modal-overlay active';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3 style="margin:0;">${title}</h3>
                <button class="close-modal" onclick="this.closest('.modal-overlay').remove()" style="background:#f1f5f9;border:none;border-radius:50%;width:32px;height:32px;display:flex;align-items:center;justify-content:center;color:#64748b;cursor:pointer;transition:0.2s;padding:0;">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                </button>
            </div>
            <div class="modal-body">
                ${contentHtml}
            </div>
            ${footerHtml ? `<div class="modal-footer" style="margin-top:24px; display:flex; justify-content:flex-end; gap:12px;">${footerHtml}</div>` : ''}
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
    const content = `
        <div style="display:flex; flex-direction:column; gap:16px;">
            <div>
                <label style="font-size:12px; color:#6b7280; font-weight:600; text-transform:uppercase;">Job Title</label>
                <div style="font-size:16px; font-weight:600; color:#111827;">${jdData.title || 'N/A'}</div>
            </div>
            <div>
                <label style="font-size:12px; color:#6b7280; font-weight:600; text-transform:uppercase;">Job Description</label>
                <div style="font-size:14px; color:#374151; line-height:1.6; white-space:pre-wrap;">${jdData.details || 'No details provided.'}</div>
            </div>
        </div>
    `;
    showModal('Job Details', content);
}

// Make available globally
window.showToast = showToast;
window.buildSidebar = buildSidebar;
window.stageBadge = stageBadge;
window.formatDate = formatDate;
window.getBasePath = getBasePath;
window.showResumeModal = showResumeModal;
window.showJdModal = showJdModal;
