auth.requireRole('PANEL');
document.getElementById('sidebar').innerHTML = buildSidebar('PANEL', 'interviews');

async function load() {
    try {
        const res = await api.getMyAssignedInterviews();
        const interviews = res.data || [];
        const container = document.getElementById('interviewList');
        container.innerHTML = interviews.length ? interviews.map(i => `
            <div class="interview-card">
                <div class="interview-card-header">
                    <div>
                        <div style="font-size:16px;font-weight:600;">${i.candidateName}</div>
                        <div style="font-size:13px;color:#64748b;margin-top:2px;">${stageBadge(i.interviewStage)}</div>
                    </div>
                    <div style="display:flex; align-items:center; gap:12px;">
                        <span style="font-size:12px;padding:4px 10px;border-radius:100px;${i.completed ? 'background:#dcfce7;color:#16a34a;' : 'background:#eef2ff;color:#4f46e5;'}">${i.completed ? ' Completed' : 'Upcoming'}</span>
                    </div>
                </div>
                <div class="interview-card-meta">
                    <span> ${formatDate(i.interviewDate)}</span>
                    <span> ${i.interviewTime || '—'}</span>
                    <span> ${(i.panelMemberNames || []).join(', ') || '—'}</span>
                </div>
                ${i.focusAreas ? `<div style="margin-top:12px;background:#f8fafc;border-radius:8px;padding:12px;font-size:13px;color:#475569;"><strong> Focus Areas:</strong> ${i.focusAreas}</div>` : ''}
                <div style="margin-top:20px; padding-top:16px; border-top:1px solid #f1f5f9; display:flex; justify-content:space-between; align-items:center;">
                    <div style="display:flex; gap:8px;">
                        <button class="btn-secondary" onclick="showResumeModal('${i.resumeUrl || ''}')" title="View Resume" style="display:flex; align-items:center; gap:6px; padding:6px 12px; font-size:13px;">
                            ${svgFileText} Resume
                        </button>
                        <button class="btn-secondary" onclick="showJdModal({title:'${i.jdTitle}', details:'${i.jdDetails?.replace(/'/g, "\\'")}'})" title="View Job Details" style="display:flex; align-items:center; gap:6px; padding:6px 12px; font-size:13px;">
                            ${svgBriefcase} Job Info
                        </button>
                    </div>
                    ${i.meetingLink ? `
                        <a href="${i.meetingLink}" target="_blank" class="btn-primary" style="display:flex; align-items:center; gap:6px; padding:6px 16px; font-size:13px; text-decoration:none;">
                            ${svgExternal} Join Meet
                        </a>
                    ` : ''}
                </div>
            </div>
        `).join('') : '<div style="text-align:center;padding:60px;color:#94a3b8;"><div style="font-size:48px;margin-bottom:16px;"></div><div>No interviews assigned yet</div></div>';
    } catch (e) {
        showToast('Failed to load: ' + e.message, 'error');
    }
}

load();
