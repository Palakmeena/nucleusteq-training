auth.requireRole('PANEL');
document.getElementById('sidebar').innerHTML = buildSidebar('PANEL', 'interviews');

// Format time to 12-hour format with AM/PM
function formatTimeWithAmPm(timeStr) {
    if (!timeStr) return '—';
    try {
        const [hours, minutes] = timeStr.split(':');
        const h = parseInt(hours);
        const m = minutes || '00';
        const ampm = h >= 12 ? 'PM' : 'AM';
        const displayHour = h % 12 || 12;
        return `${displayHour}:${m} ${ampm}`;
    } catch {
        return timeStr;
    }
}

async function load() {
    try {
        const res = await api.getMyAssignedInterviews();
        const interviews = res.data || [];
        const container = document.getElementById('interviewList');
        container.innerHTML = interviews.length ? interviews.map(i => `
            <div class="interview-card">
                <div class="interview-card-header">
                    <div>
                        <div class="interview-card-title">${i.candidateName}</div>
                        <div class="interview-stage-label">${stageBadge(i.interviewStage)}</div>
                    </div>
                    <span class="interview-card-badge">${i.completed ? ' Completed' : 'Upcoming'}</span>
                </div>
                <div class="interview-card-meta">
                    <div class="interview-card-meta-item">
                        <div class="interview-card-meta-label">Date</div>
                        <div class="interview-card-meta-value">${formatDate(i.interviewDate)}</div>
                    </div>
                    <div class="interview-card-meta-item">
                        <div class="interview-card-meta-label">Time</div>
                        <div class="interview-card-meta-value">${formatTimeWithAmPm(i.interviewTime)}</div>
                    </div>
                    <div class="interview-card-meta-item">
                        <div class="interview-card-meta-label">Panel</div>
                        <div class="interview-card-meta-value">${(i.panelMemberNames || []).join(', ') || '—'}</div>
                    </div>
                </div>
                ${i.focusAreas ? `
                    <div class="interview-card-focus">
                        <div class="interview-card-focus-label">Focus Areas</div>
                        <div class="interview-card-focus-value">${i.focusAreas}</div>
                    </div>
                ` : ''}
                <div class="interview-card-actions">
                    <button onclick="showResumeModal('${i.resumeUrl || ''}')" title="View Resume">
                        ${svgFileText} Resume
                    </button>
                    <button onclick="showJdModal({title:'${i.jdTitle}', details:'${i.jdDetails?.replace(/'/g, "\\'")}'})" title="View Job Details">
                        ${svgBriefcase} Job Info
                    </button>
                    ${i.meetingLink ? `
                        <a href="${i.meetingLink}" target="_blank" class="interview-join-link">
                            ${svgExternal} Join Meet
                        </a>
                    ` : ''}
                </div>
            </div>
        `).join('') : '<div class="interview-empty"><div class="interview-empty-icon"></div><div>No interviews assigned yet</div></div>';
    } catch (e) {
        showToast('Failed to load: ' + e.message, 'error');
    }
}

load();
