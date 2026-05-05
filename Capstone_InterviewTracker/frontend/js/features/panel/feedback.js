auth.requireRole('PANEL');
document.getElementById('sidebar').innerHTML = buildSidebar('PANEL', 'feedback');

let currentInterview = null;
let selectedRating = 0;

function getInterviewDateTime(interview) {
    if (!interview?.interviewDate || !interview?.interviewTime) {
        return null;
    }

    const value = new Date(`${interview.interviewDate}T${interview.interviewTime}`);
    return Number.isNaN(value.getTime()) ? null : value;
}

function canOpenFeedbackForm(interview) {
    const scheduledAt = getInterviewDateTime(interview);
    return scheduledAt !== null && new Date() >= scheduledAt;
}

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

async function loadCandidates() {
    try {
        const res = await api.getMyAssignedInterviews();
        const pending = (res.data || []).filter(i => !i.completed);
        const container = document.getElementById('candidateList');

        if (!pending.length) {
            container.innerHTML = '<div class="panel-feedback-loading">No pending evaluations.</div>';
            return;
        }

        container.innerHTML = pending.map(i => `
            <div class="candidate-card" id="card-${i.id}" onclick='selectCandidate(${JSON.stringify(i).replace(/"/g, "&quot;")})'>
                <div class="candidate-card-name">${i.candidateName}</div>
                <div class="candidate-card-stage">${stageBadge(i.interviewStage)}</div>
                <div class="candidate-card-date">Scheduled: ${formatDate(i.interviewDate)}</div>
            </div>
        `).join('');
    } catch (e) {
        showToast('Failed to load candidates', 'error');
    }
}

function selectCandidate(interview) {
    if (!canOpenFeedbackForm(interview)) {
        showToast('You can submit feedback only after the interview time', 'error');
        return;
    }

    currentInterview = interview;
    document.getElementById('formTitle').textContent = `Evaluate: ${interview.candidateName}`;
    document.getElementById('formSubtitle').textContent = `${interview.interviewStage} - ${formatDate(interview.interviewDate)} at ${formatTimeWithAmPm(interview.interviewTime)}`;
    document.querySelectorAll('.candidate-card').forEach(c => c.classList.remove('active'));
    document.getElementById(`card-${interview.id}`).classList.add('active');
    resetForm();
    openFeedbackModal();
}

function openFeedbackModal() {
    document.getElementById('feedbackModal').classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeFeedbackModal() {
    document.getElementById('feedbackModal').classList.remove('active');
    document.body.style.overflow = 'auto';
    resetForm();
}

function setRating(val) {
    selectedRating = val;
    document.querySelectorAll('.rating-btn').forEach((btn, i) => {
        btn.classList.toggle('selected', (i + 1) === val);
    });
}

function resetForm() {
    selectedRating = 0;
    document.querySelectorAll('.rating-btn').forEach(btn => btn.classList.remove('selected'));
    document.getElementById('comments').value = '';
    document.getElementById('strengths').value = '';
    document.getElementById('weaknesses').value = '';
    document.getElementById('decision').value = '';
}

async function submitFeedback() {
    const comments = document.getElementById('comments').value;
    const decision = document.getElementById('decision').value;

    if (!selectedRating || !comments || !decision) {
        showToast('Rating, Comments and Decision are mandatory', 'error');
        return;
    }

    try {
        const body = {
            interviewId: currentInterview.id,
            rating: selectedRating,
            comments,
            strengths: document.getElementById('strengths').value,
            weaknesses: document.getElementById('weaknesses').value,
            decision
        };

        await api.updateInterviewFeedback(currentInterview.id, body);

        showToast('Feedback submitted successfully', 'success');
        closeFeedbackModal();
        loadCandidates();
    } catch (e) {
        showToast('Failed to submit: ' + e.message, 'error');
    }
}

window.selectCandidate = selectCandidate;

// Setup event listeners for modal controls
document.addEventListener('DOMContentLoaded', () => {
    const modalCloseBtn = document.getElementById('modalCloseBtn');
    const submitBtn = document.getElementById('submitBtn');
    const ratingGroup = document.getElementById('ratingGroup');

    // Close button listener
    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', closeFeedbackModal);
    }

    // Submit button listener
    if (submitBtn) {
        submitBtn.addEventListener('click', submitFeedback);
    }

    // Rating buttons - use event delegation on parent
    if (ratingGroup) {
        ratingGroup.addEventListener('click', (e) => {
            if (e.target.classList.contains('rating-btn')) {
                const rating = parseInt(e.target.getAttribute('data-rating'), 10);
                setRating(rating);
            }
        });
    }
});

// Close modal when clicking outside
document.getElementById('feedbackModal')?.addEventListener('click', (e) => {
    if (e.target.id === 'feedbackModal') {
        closeFeedbackModal();
    }
});

loadCandidates();
