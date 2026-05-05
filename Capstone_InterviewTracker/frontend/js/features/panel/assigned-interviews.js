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
                    <button onclick="openJobInfo('${i.jdId || ''}','${(i.jdTitle||'').replace(/'/g,"\\'")}', '${(i.jdDetails||'').replace(/'/g,"\\'")}')" title="View Job Details">
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

// Fetch full JD by id when possible and show modal. Falls back to provided title/details.
async function openJobInfo(jdId, fallbackTitle, fallbackDetails) {
    try {
        if (jdId) {
            const res = await api.getJdById(jdId);
            const jd = res.data || {};
            // Normalize keys expected by showJdModal
            const jdData = {
                jobTitle: jd.jobTitle || jd.title || fallbackTitle || 'N/A',
                location: jd.location || jd.city || jd.address || 'N/A',
                jobType: jd.jobType || jd.type || 'N/A',
                minExperience: jd.minExperience || jd.minExp || jd.min || 0,
                maxExperience: jd.maxExperience || jd.maxExp || jd.max || 'N/A',
                minSalary: jd.minSalary || jd.minSalaryLpa || jd.minSal || 0,
                maxSalary: jd.maxSalary || jd.maxSalaryLpa || jd.maxSal || 'N/A',
                skills: jd.skills || jd.skillSet || [],
                jobDescription: jd.jobDescription || jd.details || fallbackDetails || ''
            };
            showJdModal(jdData);
            return;
        }
    } catch (e) {
        // ignore and fallback to simple title/details
        console.warn('Failed to fetch JD by id:', e.message);
    }
    // fallback when no jdId or fetch fails
    showJdModal({ title: fallbackTitle || 'N/A', details: fallbackDetails || 'No details provided.' });
}
