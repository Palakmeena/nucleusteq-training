auth.requireRole('HR');
document.getElementById('sidebar').innerHTML = buildSidebar('HR', 'overview');

async function loadOverview() {
    try {
        const [cRes, jRes, pRes] = await Promise.all([
            api.getAllCandidates(),
            api.getAllJdsForHr(),
            api.getAllPanelMembers()
        ]);

        const candidates = cRes.data || [];
        const jds = jRes.data || [];
        const panels = pRes.data || [];

        document.getElementById('totalCandidates').textContent = candidates.length;
        document.getElementById('activeJds').textContent = jds.filter(j => j.active).length;
        document.getElementById('totalPanels').textContent = panels.length;
        document.getElementById('inProgress').textContent = candidates.filter(c => !['SELECTED', 'REJECTED'].includes(c.currentStage)).length;

        ['PROFILING', 'SCREENING', 'L1_TECHNICAL', 'L2_TECHNICAL', 'HR_ROUND'].forEach(stage => {
            const el = document.getElementById('cnt-' + stage);
            if (el) el.textContent = candidates.filter(c => c.currentStage === stage).length + ' candidates';
        });

        const tbody = document.getElementById('recentCandidates');
        const recent = candidates.slice(0, 6);
        tbody.innerHTML = recent.length ? recent.map(c => `
            <tr>
                <td>
                    <div class="overview-candidate-row">
                        <div class="overview-candidate-avatar">
                            ${c.fullName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                        </div>
                        <div class="overview-candidate-info">
                            <div class="overview-candidate-name">${c.fullName}</div>
                            <div class="overview-candidate-email">${c.email}</div>
                        </div>
                    </div>
                </td>
                <td class="overview-table-cell">${c.jobTitle || '—'}</td>
                <td class="overview-table-cell">${c.totalExperience} yrs</td>
                <td>${stageBadge(c.currentStage)}</td>
                <td><a href="candidates.html" class="overview-view-link">View</a></td>
            </tr>
        `).join('') : '<tr><td colspan="5" class="overview-empty-row">No candidates yet</td></tr>';

    } catch (e) {
        showToast('Failed to load dashboard: ' + e.message, 'error');
    }
}

loadOverview();
