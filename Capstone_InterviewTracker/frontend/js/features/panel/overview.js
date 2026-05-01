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
                <td style="font-weight:500;">${i.candidateName}</td>
                <td>${stageBadge(i.interviewStage)}</td>
                <td style="font-size:13px;">${formatDate(i.interviewDate)}</td>
                <td style="font-size:13px;">${i.interviewTime || '—'}</td>
                <td style="font-size:13px;color:#64748b;">${i.focusAreas || '—'}</td>
            </tr>
        `).join('') : '<tr><td colspan="5" style="text-align:center;padding:40px;color:#94a3b8;">No interviews assigned yet</td></tr>';
    } catch (e) {
        showToast('Failed to load: ' + e.message, 'error');
    }
}

load();
