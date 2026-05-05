auth.requireRole('PANEL');
document.getElementById('sidebar').innerHTML = buildSidebar('PANEL', 'overview');
document.getElementById('welcomeText').textContent = 'Welcome, ' + auth.getFullName() + '!';

async function load() {
    try {
        const res = await api.getMyAssignedInterviews();
        const interviews = res.data || [];
        document.getElementById('totalInterviews').textContent = interviews.length;
        document.getElementById('upcoming').textContent = interviews.filter(i => !i.completed).length;
        document.getElementById('completed').textContent = interviews.filter(i => i.completed).length;
        const tbody = document.getElementById('interviewBody');
        tbody.innerHTML = interviews.length ? interviews.slice(0, 5).map(i => `
            <tr>
                <td class="panel-overview-candidate-name">${i.candidateName}</td>
                <td>${stageBadge(i.interviewStage)}</td>
                <td class="panel-overview-interview-date">${formatDate(i.interviewDate)}</td>
                <td class="panel-overview-interview-time">${i.interviewTime || '—'}</td>
                <td class="panel-overview-focus-areas">${i.focusAreas || '—'}</td>
            </tr>
        `).join('') : '<tr><td colspan="5" class="panel-overview-empty">No interviews assigned yet</td></tr>';
    } catch (e) {
        showToast('Failed to load: ' + e.message, 'error');
    }
}

load();
