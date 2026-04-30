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

// SVG ICONS
const svgDashboard = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>`;
const svgBriefcase = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>`;
const svgUsers     = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`;
const svgCalendar  = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>`;
const svgUser      = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`;
const svgSettings  = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>`;
const svgHome      = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`;
const svgLogout    = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>`;
const svgFileText  = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>`;
const svgEye       = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>`;
const svgDownload  = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>`;
const svgExternal  = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>`;
const svgEdit      = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>`;
const svgTrash     = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>`;
const svgX         = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>`;
const svgPlus      = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>`;
const svgCheck     = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>`;
const svgUserCheck = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><polyline points="16 11 18 13 22 9"></polyline></svg>`;


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
                <h3>${title}</h3>
                <button class="close-modal" onclick="this.closest('.modal-overlay').remove()">&times;</button>
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
