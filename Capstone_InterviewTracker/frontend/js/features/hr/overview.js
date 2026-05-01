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
                    <div style="display:flex;align-items:center;gap:10px;">
                        <div style="width:32px;height:32px;border-radius:50%;background:#eef2ff;color:#4f46e5;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:12px;flex-shrink:0;">
                            ${c.fullName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                        </div>
                        <div>
                            <div style="font-weight:500;font-size:14px;">${c.fullName}</div>
                            <div style="font-size:12px;color:#64748b;">${c.email}</div>
                        </div>
                    </div>
                </td>
                <td style="font-size:14px;">${c.jobTitle || '—'}</td>
                <td style="font-size:14px;">${c.totalExperience} yrs</td>
                <td>${stageBadge(c.currentStage)}</td>
                <td><a href="candidates.html" style="font-size:13px;color:#4f46e5;text-decoration:none;padding:4px 10px;border:1px solid #4f46e5;border-radius:6px;">View</a></td>
            </tr>
        `).join('') : '<tr><td colspan="5" style="text-align:center;padding:32px;color:#94a3b8;">No candidates yet</td></tr>';

    } catch (e) {
        showToast('Failed to load dashboard: ' + e.message, 'error');
    }
}

loadOverview();
