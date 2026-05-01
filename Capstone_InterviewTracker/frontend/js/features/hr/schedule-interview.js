auth.requireRole('HR');
document.getElementById('sidebar').innerHTML = buildSidebar('HR', 'interviews');

let allPanels = [];
let selectedPanels = [];

const params = new URLSearchParams(window.location.search);
const preselectedId = params.get('candidateId');

async function loadForm() {
    loadUpcoming();
}

function togglePanel(id) {
    const idx = selectedPanels.indexOf(id);
    if (idx > -1) {
        selectedPanels.splice(idx, 1);
    } else {
        if (selectedPanels.length >= 2) {
            showToast('Maximum 2 panel members allowed', 'error');
            return;
        }
        selectedPanels.push(id);
    }

    updateSelectedPanelsDisplay();
    filterPanels();
}

function updateSelectedPanelsDisplay() {
    const display = document.getElementById('selectedPanelsDisplay');
    const selectedList = document.getElementById('selectedList');

    if (selectedPanels.length === 0) {
        display.style.display = 'none';
        return;
    }

    display.style.display = 'block';

    const selectedItems = selectedPanels.map(panelId => {
        const panel = allPanels.find(p => p.id === panelId);
        return `
            <div style="background:#fff;padding:8px 12px;border-radius:6px;display:flex;justify-content:space-between;align-items:center;">
                <div>
                    <div style="font-size:13px;font-weight:500;color:#1e293b;">${panel.fullName}</div>
                    <div style="font-size:11px;color:#64748b;">${panel.designation}</div>
                </div>
                <button style="background:none;border:none;color:#dc2626;font-size:18px;cursor:pointer;padding:0;width:20px;height:20px; display:flex; align-items:center; justify-content:center;" onclick="togglePanel(${panelId})">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </div>
        `;
    }).join('');

    selectedList.innerHTML = selectedItems;
}

function filterPanels() {
    const searchTerm = document.getElementById('panelSearchInput').value.toLowerCase().trim();
    const container = document.getElementById('panelOptions');

    if (!searchTerm) {
        container.innerHTML = '<div style="color:#94a3b8;font-size:13px;padding:20px;text-align:center;">Type to search for panel members...</div>';
        return;
    }

    const filtered = allPanels.filter(p => p.fullName.toLowerCase().startsWith(searchTerm));

    if (filtered.length === 0) {
        container.innerHTML = '<div style="color:#94a3b8;font-size:13px;padding:20px;text-align:center;">No panel members found</div>';
        return;
    }

    const topTwo = filtered.slice(0, 2);

    container.innerHTML = topTwo.map(p => `
        <div class="panel-option" id="po-${p.id}" onclick="togglePanel(${p.id})" ${selectedPanels.includes(p.id) ? 'style="border-color:#4f46e5;background:#eef2ff;"' : ''}>
            <input type="checkbox" id="pc-${p.id}" ${selectedPanels.includes(p.id) ? 'checked' : ''}>
            <div>
                <div style="font-size:14px;font-weight:500;">${p.fullName}</div>
                <div style="font-size:12px;color:#64748b;">${p.designation} — ${p.organization} ${p.active ? '' : '(Pending Activation)'}</div>
            </div>
        </div>
    `).join('');
}

async function scheduleInterview() {
    const errDiv = document.getElementById('scheduleError');
    errDiv.style.display = 'none';

    const candidateId = parseInt(document.getElementById('candidateSelect').value);
    const interviewStage = document.getElementById('stageSelect').value;
    const interviewDate = document.getElementById('interviewDate').value;
    const interviewTime = document.getElementById('interviewTime').value;
    const focusAreas = document.getElementById('focusAreas').value;
    const meetingLink = document.getElementById('meetingLink').value;

    if (!candidateId || !interviewStage || !interviewDate || !interviewTime || !selectedPanels.length) {
        errDiv.textContent = 'Please fill all required fields and select at least one panel member';
        errDiv.style.display = 'block';
        return;
    }

    try {
        const body = {
            candidateId,
            interviewStage,
            interviewDate,
            interviewTime,
            focusAreas,
            meetingLink,
            panelMemberIds: selectedPanels
        };
        await api.scheduleInterview(body);
        showToast('Interview scheduled successfully!', 'success');

        document.getElementById('meetingLink').value = '';
        document.getElementById('focusAreas').value = '';
        selectedPanels = [];
        updateSelectedPanelsDisplay();

        loadUpcoming();
    } catch (e) {
        errDiv.textContent = e.message || 'Failed to schedule interview. Please check the sequence.';
        errDiv.style.display = 'block';
        showToast(e.message, 'error');
    }
}

async function loadUpcoming() {
    try {
        const cRes = await api.getAllCandidates();
        const candidates = cRes.data || [];
        const container = document.getElementById('upcomingList');
        const stageLabels = { L1_TECHNICAL: 'L1 Technical', L2_TECHNICAL: 'L2 Technical', HR_ROUND: 'HR Round' };

        const activeCandidates = candidates.filter(c => c.currentStage !== 'REJECTED');

        const allInterviews = [];
        for (const c of activeCandidates) {
            try {
                const iRes = await api.getInterviewsForCandidate(c.id);
                const interviews = iRes.data || [];

                const latestByStage = {};
                interviews.forEach(i => {
                    const existing = latestByStage[i.interviewStage];
                    if (!existing || new Date(i.interviewDate) > new Date(existing.interviewDate)) {
                        latestByStage[i.interviewStage] = i;
                    }
                });

                Object.values(latestByStage).forEach(i => allInterviews.push({ ...i, candidateName: c.fullName }));
            } catch (e) { }
        }

        allInterviews.sort((a, b) => new Date(b.interviewDate) - new Date(a.interviewDate));

        container.innerHTML = allInterviews.length ? allInterviews.map(i => {
            const isToday = new Date().toDateString() === new Date(i.interviewDate).toDateString();
            const isPast = new Date() > new Date(i.interviewDate) && !isToday;

            let statusBadge = '<span style="font-size:12px;font-weight:600;background:#eef2ff;color:#4f46e5;padding:4px 12px;border-radius:100px;">Upcoming</span>';
            if (i.completed) statusBadge = '<span style="font-size:12px;font-weight:600;background:#dcfce7;color:#16a34a;padding:4px 12px;border-radius:100px;">Completed</span>';
            else if (isToday) statusBadge = '<span style="font-size:12px;font-weight:600;background:#fef3c7;color:#d97706;padding:4px 12px;border-radius:100px;">Today</span>';
            else if (isPast) statusBadge = '<span style="font-size:12px;font-weight:600;background:#fee2e2;color:#991b1b;padding:4px 12px;border-radius:100px;">Overdue</span>';

            return `
                <div style="background:#fff;border:1px solid #e2e8f0;border-radius:12px;padding:24px;margin-bottom:16px;box-shadow:0 1px 3px rgba(0,0,0,0.02);transition:0.2s;">
                    <div style="display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:16px;">
                        <div>
                            <div style="font-weight:700;font-size:18px;color:#1e293b;margin-bottom:8px;">${i.candidateName}</div>
                            <div style="display:flex;align-items:center;gap:10px;">
                                <div style="font-size:13px;font-weight:600;color:#64748b;background:#f1f5f9;padding:4px 12px;border-radius:100px;border:1px solid #e2e8f0;">${stageLabels[i.interviewStage] || i.interviewStage}</div>
                                ${statusBadge}
                            </div>
                        </div>
                        <div style="text-align:right;background:#f8fafc;padding:10px 16px;border-radius:10px;border:1px solid #e2e8f0;">
                            <div style="font-size:14px;font-weight:700;color:#334155;margin-bottom:4px;">
                                ${formatDate(i.interviewDate)}
                            </div>
                            <div style="font-size:13px;color:#64748b;font-weight:600;">
                                ${formatInterviewTime(i.interviewTime)}
                            </div>
                        </div>
                    </div>
                    
                    <div style="margin-top:20px; padding-top:20px; border-top:1px solid #f1f5f9; display:flex; justify-content:flex-end; gap:12px;">
                        <button onclick="viewInterviewInfo(${JSON.stringify(i).replace(/"/g, '&quot;')})" class="secondary-btn" style="padding:10px 20px;font-size:13px;font-weight:600;width:auto;height:auto;line-height:1.2;">
                            View Details
                        </button>
                        ${i.completed ? `
                            <button onclick="viewFeedback(${JSON.stringify(i).replace(/"/g, '&quot;')})" class="primary-btn" style="padding:10px 20px;font-size:13px;font-weight:600;width:auto;height:auto;line-height:1.2;">
                                See Feedback
                            </button>
                        ` : ''}
                        ${(!i.completed && i.interviewStage === 'HR_ROUND') ? `
                            <button onclick="openHrFeedbackModal(${JSON.stringify(i).replace(/"/g, '&quot;')})" class="primary-btn" style="background:#16a34a;border-color:#16a34a;padding:10px 20px;font-size:13px;font-weight:600;width:auto;height:auto;line-height:1.2;">
                                Give Feedback
                            </button>
                        ` : ''}
                    </div>
                </div>
            `}).join('') : '<div style="text-align:center;padding:60px 40px;color:#94a3b8;font-size:15px;background:#f8fafc;border-radius:12px;border:2px dashed #e2e8f0;">No interviews scheduled</div>';
    } catch (e) {
        console.error(e);
    }
}

function viewInterviewInfo(i) {
    document.getElementById('infoModal').classList.add('active');

    const stageLabels = { L1_TECHNICAL: 'L1 Technical', L2_TECHNICAL: 'L2 Technical', HR_ROUND: 'HR Round' };

    document.getElementById('infoCandidate').textContent = i.candidateName;
    document.getElementById('infoStage').textContent = stageLabels[i.interviewStage] || i.interviewStage;
    document.getElementById('infoDateTime').innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:#64748b;"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg> <span>${formatDate(i.interviewDate)} &nbsp;&bull;&nbsp; ${formatInterviewTime(i.interviewTime)}</span>`;

    const panelNames = (i.panelMemberNames && i.panelMemberNames.length > 0) ? i.panelMemberNames.join(' &nbsp;&bull;&nbsp; ') : (i.interviewStage === 'HR_ROUND' ? 'HR (Self)' : 'No panels assigned');
    document.getElementById('infoPanels').innerHTML = panelNames;

    document.getElementById('infoLink').innerHTML = i.meetingLink
        ? `<a href="${i.meetingLink}" target="_blank" style="display:inline-flex;align-items:center;gap:8px;color:#4f46e5;font-weight:600;text-decoration:none;"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path></svg> Join Meeting Online</a>`
        : '<span style="color:#94a3b8;font-style:italic;display:flex;align-items:center;gap:6px;"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line></svg> No link provided</span>';
}

function viewFeedback(i) {
    document.getElementById('feedbackModal').classList.add('active');

    const stageLabels = { L1_TECHNICAL: 'L1 Technical', L2_TECHNICAL: 'L2 Technical', HR_ROUND: 'HR Round' };

    document.getElementById('fCandidate').textContent = i.candidateName;
    document.getElementById('fStage').textContent = stageLabels[i.interviewStage] || i.interviewStage;

    const container = document.getElementById('feedbackList');
    if (!i.feedbacks || i.feedbacks.length === 0) {
        container.innerHTML = '<div style="text-align:center;padding:40px;color:#94a3b8;background:#fff;border-radius:12px;border:1px dashed #e2e8f0;">No detailed evaluations submitted yet.</div>';
        return;
    }

    container.innerHTML = i.feedbacks.map(f => {
        const isSelected = f.decision === 'SELECTED';
        const decisionColor = isSelected ? '#16a34a' : '#ef4444';
        const decisionBg = isSelected ? '#dcfce7' : '#fee2e2';

        return `
            <div style="border:1px solid #e2e8f0;border-radius:8px;padding:24px;margin-bottom:20px;text-align:left;background:#fff;">
                <div style="display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #f1f5f9;padding-bottom:16px;margin-bottom:20px;">
                    <div>
                        <div style="font-size:16px;font-weight:700;color:#0f172a;margin-bottom:4px;">${f.panelMemberName}</div>
                        <div style="font-size:13px;color:#64748b;">${new Date(f.submittedAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' })}</div>
                    </div>
                    <div style="text-align:right;">
                        <div style="font-size:20px;font-weight:800;color:#0f172a;margin-bottom:6px;">${f.rating}/5</div>
                        <div style="display:inline-block;font-size:11px;font-weight:700;text-transform:uppercase;color:${decisionColor};background:${decisionBg};padding:4px 10px;border-radius:4px;">${f.decision}</div>
                    </div>
                </div>
                <div style="display:flex;flex-direction:column;gap:20px;">
                    <div>
                        <label style="display:block;font-size:12px;font-weight:600;color:#0f172a;text-transform:uppercase;margin-bottom:6px;">Suggestion / Recommendation</label>
                        <div style="font-size:14px;color:#334155;line-height:1.6;white-space:pre-wrap;">${f.decision || '<span style="color:#94a3b8;font-style:italic;">—</span>'}</div>
                    </div>
                    <div>
                        <label style="display:block;font-size:12px;font-weight:600;color:#0f172a;text-transform:uppercase;margin-bottom:6px;">Main Comments</label>
                        <div style="font-size:14px;color:#334155;line-height:1.6;white-space:pre-wrap;">${f.comments}</div>
                    </div>
                    <div>
                        <label style="display:block;font-size:12px;font-weight:600;color:#0f172a;text-transform:uppercase;margin-bottom:6px;">Key Strengths</label>
                        <div style="font-size:14px;color:#334155;line-height:1.6;white-space:pre-wrap;">${f.strengths || '<span style="color:#94a3b8;font-style:italic;">—</span>'}</div>
                    </div>
                    <div>
                        <label style="display:block;font-size:12px;font-weight:600;color:#0f172a;text-transform:uppercase;margin-bottom:6px;">Areas of Improvement</label>
                        <div style="font-size:14px;color:#334155;line-height:1.6;white-space:pre-wrap;">${f.weaknesses || '<span style="color:#94a3b8;font-style:italic;">—</span>'}</div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function formatInterviewTime(timeValue) {
    if (!timeValue) return 'TBD';

    const parts = timeValue.split(':');
    if (parts.length < 2) return timeValue;

    const hours = parseInt(parts[0], 10);
    if (Number.isNaN(hours)) return timeValue;

    const minutes = parts[1];
    const displayHours = hours % 12 || 12;
    const period = hours >= 12 ? 'PM' : 'AM';
    return `${displayHours}:${minutes} ${period}`;
}

let currentHrFeedbackInterviewId = null;

function openHrFeedbackModal(i) {
    currentHrFeedbackInterviewId = i.id;
    document.getElementById('hrfCandidate').textContent = i.candidateName;
    document.getElementById('hrFeedbackError').style.display = 'none';
    document.getElementById('hrFeedbackForm').reset();
    document.getElementById('hrFeedbackModal').classList.add('active');
}

async function submitHrFeedback() {
    const errDiv = document.getElementById('hrFeedbackError');
    errDiv.style.display = 'none';

    const rating = document.getElementById('hrfRating').value;
    const comments = document.getElementById('hrfComments').value.trim();
    const strengths = document.getElementById('hrfStrengths').value.trim();
    const weaknesses = document.getElementById('hrfWeaknesses').value.trim();
    const decision = document.getElementById('hrfDecision').value;

    if (!rating || !comments || !decision) {
        errDiv.textContent = 'Please fill Rating, Comments and Decision';
        errDiv.style.display = 'block';
        return;
    }

    const btn = document.getElementById('hrFeedbackBtn');
    btn.disabled = true;
    btn.textContent = 'Submitting...';

    try {
        await api.submitHrFeedback(currentHrFeedbackInterviewId, {
            rating: parseInt(rating),
            comments,
            strengths: strengths || 'N/A',
            weaknesses: weaknesses || 'N/A',
            decision
        });
        showToast('HR feedback submitted successfully!', 'success');
        document.getElementById('hrFeedbackModal').classList.remove('active');
        loadUpcoming();
    } catch (e) {
        errDiv.textContent = e.message || 'Failed to submit feedback';
        errDiv.style.display = 'block';
    } finally {
        btn.disabled = false;
        btn.textContent = 'Submit Feedback';
    }
}

window.togglePanel = togglePanel;
window.filterPanels = filterPanels;
window.scheduleInterview = scheduleInterview;
window.viewInterviewInfo = viewInterviewInfo;
window.viewFeedback = viewFeedback;
window.openHrFeedbackModal = openHrFeedbackModal;
window.submitHrFeedback = submitHrFeedback;

loadForm();
loadUpcoming();
