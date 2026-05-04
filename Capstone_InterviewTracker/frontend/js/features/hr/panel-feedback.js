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
            <div class="selected-panel-item">
                <div class="selected-panel-info">
                    <div class="name">${panel.fullName}</div>
                    <div class="designation">${panel.designation}</div>
                </div>
                <button class="selected-panel-remove-btn" onclick="togglePanel(${panelId})">
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
        container.innerHTML = '<div class="panel-empty-msg">Type to search for panel members...</div>';
        return;
    }

    const filtered = allPanels.filter(p => p.fullName.toLowerCase().startsWith(searchTerm));

    if (filtered.length === 0) {
        container.innerHTML = '<div class="panel-empty-msg">No panel members found</div>';
        return;
    }

    const topTwo = filtered.slice(0, 2);

    container.innerHTML = topTwo.map(p => `
        <div class="panel-option" id="po-${p.id}" onclick="togglePanel(${p.id})" ${selectedPanels.includes(p.id) ? 'class="panel-option panel-option-selected"' : ''}>
            <input type="checkbox" id="pc-${p.id}" ${selectedPanels.includes(p.id) ? 'checked' : ''}>
            <div>
                <div class="panel-option-name">${p.fullName}</div>
                <div class="panel-option-designation">${p.designation} — ${p.organization} ${p.active ? '' : '(Pending Activation)'}</div>
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

            let statusBadge = '<span class="status-badge status-upcoming">Upcoming</span>';
            if (i.completed) statusBadge = '<span class="status-badge status-completed">Completed</span>';
            else if (isToday) statusBadge = '<span class="status-badge status-today">Today</span>';
            else if (isPast) statusBadge = '<span class="status-badge status-overdue">Overdue</span>';

            return `
                <div class="interview-card">
                    <div class="interview-card-header">
                        <div>
                            <div class="interview-card-title-main">${i.candidateName}</div>
                            <div class="interview-card-header-badges">
                                <div class="stage-pill">${stageLabels[i.interviewStage] || i.interviewStage}</div>
                                ${statusBadge}
                            </div>
                        </div>
                        <div class="interview-card-datetime">
                            <div class="interview-card-datetime-label">
                                ${formatDate(i.interviewDate)}
                            </div>
                            <div class="interview-card-datetime-time">
                                ${formatInterviewTime(i.interviewTime)}
                            </div>
                        </div>
                    </div>
                    
                    <div class="interview-card-actions">
                        <button onclick="viewInterviewInfo(${JSON.stringify(i).replace(/"/g, '&quot;')})" class="secondary-btn action-btn">
                            View Details
                        </button>
                        ${(i.completed || (i.feedbacks && i.feedbacks.length > 0)) ? `
                            <button onclick="viewFeedback(${JSON.stringify(i).replace(/"/g, '&quot;')})" class="primary-btn action-btn">
                                See Feedback
                            </button>
                        ` : ''}
                        ${(!i.completed && i.interviewStage === 'HR_ROUND') ? `
                            <button onclick="openHrFeedbackModal(${JSON.stringify(i).replace(/"/g, '&quot;')})" class="primary-btn action-btn primary-strong">
                                Give Feedback
                            </button>
                        ` : ''}
                    </div>
                </div>
            `}).join('') : '<div class="no-interviews-empty">No interviews scheduled</div>';
    } catch (e) {
        console.error(e);
    }
}

function viewInterviewInfo(i) {
    document.getElementById('infoModal').classList.add('active');

    const stageLabels = { L1_TECHNICAL: 'L1 Technical', L2_TECHNICAL: 'L2 Technical', HR_ROUND: 'HR Round' };

    document.getElementById('infoCandidate').textContent = i.candidateName;
    document.getElementById('infoStage').textContent = stageLabels[i.interviewStage] || i.interviewStage;
    document.getElementById('infoDateTime').innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-gray"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg> <span>${formatDate(i.interviewDate)} &nbsp;&bull;&nbsp; ${formatInterviewTime(i.interviewTime)}</span>`;

    const panelNames = (i.panelMemberNames && i.panelMemberNames.length > 0) ? i.panelMemberNames.join(' &nbsp;&bull;&nbsp; ') : (i.interviewStage === 'HR_ROUND' ? 'HR (Self)' : 'No panels assigned');
    document.getElementById('infoPanels').innerHTML = panelNames;

    document.getElementById('infoLink').innerHTML = i.meetingLink
        ? `<a href="${i.meetingLink}" target="_blank" class="interview-meeting-link"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path></svg> Join Meeting Online</a>`
        : '<span class="interview-no-link"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line></svg> No link provided</span>';
}

function viewFeedback(i) {
    document.getElementById('feedbackModal').classList.add('active');

    const stageLabels = { L1_TECHNICAL: 'L1 Technical', L2_TECHNICAL: 'L2 Technical', HR_ROUND: 'HR Round' };

    document.getElementById('fCandidate').textContent = i.candidateName;
    document.getElementById('fStage').textContent = stageLabels[i.interviewStage] || i.interviewStage;

    const container = document.getElementById('feedbackList');
    if (!i.feedbacks || i.feedbacks.length === 0) {
        container.innerHTML = '<div class="feedback-empty">No panel feedback has been submitted yet.</div>';
        return;
    }

    container.innerHTML = i.feedbacks.map(f => {
        return `
            <div class="feedback-card">
                <div class="feedback-card-header">
                    <div>
                        <div class="feedback-card-header name">${f.panelMemberName || 'Panel Member'}</div>
                        <div class="feedback-card-header date">${new Date(f.submittedAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' })}</div>
                    </div>
                    <div>
                        <div class="feedback-rating">${f.rating}/5</div>
                        <div class="feedback-decision ${f.decision === 'SELECTED' ? 'selected' : 'rejected'}">${f.decision}</div>
                    </div>
                </div>
                <div class="feedback-section">
                    <div>
                        <label class="feedback-section label">Panel Suggestion</label>
                        <div class="feedback-section content">${f.panelSuggestion || '<span class="feedback-content-empty">—</span>'}</div>
                    </div>
                    <div>
                        <label class="feedback-section label">Main Comments</label>
                        <div class="feedback-section content">${f.comments}</div>
                    </div>
                    <div>
                        <label class="feedback-section label">Key Strengths</label>
                        <div class="feedback-section content">${f.strengths || '<span class="feedback-content-empty">—</span>'}</div>
                    </div>
                    <div>
                        <label class="feedback-section label">Areas of Improvement</label>
                        <div class="feedback-section content">${f.weaknesses || '<span class="feedback-content-empty">—</span>'}</div>
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

function getInterviewDateTime(interview) {
    if (!interview?.interviewDate || !interview?.interviewTime) {
        return null;
    }

    const value = new Date(`${interview.interviewDate}T${interview.interviewTime}`);
    return Number.isNaN(value.getTime()) ? null : value;
}

function canOpenHrFeedback(interview) {
    const scheduledAt = getInterviewDateTime(interview);
    return scheduledAt !== null && new Date() >= scheduledAt;
}

function openHrFeedbackModal(i) {
    if (!canOpenHrFeedback(i)) {
        showToast('You can give feedback only after the interview time', 'error');
        return;
    }

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
