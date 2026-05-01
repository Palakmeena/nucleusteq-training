auth.requireRole('PANEL');
document.getElementById('sidebar').innerHTML = buildSidebar('PANEL', 'feedback');

let currentInterview = null;
let selectedRating = 0;

async function loadCandidates() {
    try {
        const res = await api.getMyAssignedInterviews();
        const pending = (res.data || []).filter(i => !i.completed);
        const container = document.getElementById('candidateList');

        if (!pending.length) {
            container.innerHTML = '<div style="text-align:center; padding:40px; color:#94a3b8;">No pending evaluations.</div>';
            return;
        }

        container.innerHTML = pending.map(i => `
            <div class="candidate-card" id="card-${i.id}" onclick='selectCandidate(${JSON.stringify(i).replace(/"/g, "&quot;")})'>
                <div style="font-weight:600; font-size:14px;">${i.candidateName}</div>
                <div style="font-size:12px; color:#64748b; margin-top:2px;">${stageBadge(i.interviewStage)}</div>
                <div style="font-size:11px; color:#94a3b8; margin-top:6px;">Scheduled: ${formatDate(i.interviewDate)}</div>
            </div>
        `).join('');
    } catch (e) {
        showToast('Failed to load candidates', 'error');
    }
}

function selectCandidate(interview) {
    currentInterview = interview;
    document.getElementById('emptyState').style.display = 'none';
    document.getElementById('feedbackFormArea').style.display = 'block';
    document.getElementById('formTitle').textContent = `Evaluate: ${interview.candidateName}`;
    document.querySelectorAll('.candidate-card').forEach(c => c.classList.remove('active'));
    document.getElementById(`card-${interview.id}`).classList.add('active');
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
    document.getElementById('status').value = 'PASSED';
}

async function submitFeedback() {
    const comments = document.getElementById('comments').value;
    const status = document.getElementById('status').value;

    if (!selectedRating || !comments) {
        showToast('Rating and Comments are mandatory', 'error');
        return;
    }

    try {
        const body = {
            interviewId: currentInterview.id,
            rating: selectedRating,
            comments,
            strengths: document.getElementById('strengths').value,
            weaknesses: document.getElementById('weaknesses').value,
            status
        };

        await api.updateInterviewFeedback(currentInterview.id, body);

        showToast('Feedback submitted successfully', 'success');
        document.getElementById('feedbackFormArea').style.display = 'none';
        document.getElementById('emptyState').style.display = 'block';
        loadCandidates();
    } catch (e) {
        showToast('Failed to submit: ' + e.message, 'error');
    }
}

window.selectCandidate = selectCandidate;
window.setRating = setRating;
window.submitFeedback = submitFeedback;

loadCandidates();
