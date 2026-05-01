function selectCandidate(el) {
    document.querySelectorAll('.schedule-item').forEach(item => item.classList.remove('active'));
    el.classList.add('active');
}

function selectRating(btn, val) {
    document.querySelectorAll('.rating-btn').forEach(item => item.classList.remove('selected'));
    btn.classList.add('selected');
}

function selectDecision(btn, type) {
    document.querySelectorAll('.decision-btn').forEach(item => {
        item.classList.remove('selected-yes');
        item.classList.remove('selected-no');
    });
    btn.classList.add(type === 'yes' ? 'selected-yes' : 'selected-no');
}

document.getElementById('feedbackForm').addEventListener('submit', function(e) {
    e.preventDefault();
    alert('Feedback submitted successfully!');
});

document.addEventListener('DOMContentLoaded', () => {
    const session = window.HireTrackDashboard.bootstrapDashboard('PANEL', {
        roleLabel: 'Panel',
        defaultTitle: 'Panel Member',
        avatarFallback: 'PM'
    });
    if (!session) {
        return;
    }
});

window.selectCandidate = selectCandidate;
window.selectRating = selectRating;
window.selectDecision = selectDecision;
