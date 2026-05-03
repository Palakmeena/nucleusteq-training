auth.requireRole('PANEL');
document.getElementById('sidebar').innerHTML = buildSidebar('PANEL', 'settings');

async function load() {
    try {
        const res = await api.getMyPanelProfile();
        const p = res.data;
        document.getElementById('profileDetails').innerHTML = `
            <div class="panel-details-grid">
                <div class="panel-details-row"><div class="panel-details-label">Full Name</div><div class="panel-details-value">${p.fullName}</div></div>
                <div class="panel-details-row"><div class="panel-details-label">Email</div><div class="panel-details-value-plain">${p.email}</div></div>
                <div class="panel-details-row"><div class="panel-details-label">Organization</div><div class="panel-details-value-plain">${p.organization}</div></div>
                <div class="panel-details-row"><div class="panel-details-label">Designation</div><div class="panel-details-value-plain">${p.designation}</div></div>
                <div class="panel-details-row"><div class="panel-details-label">Status</div><div class="panel-details-value-plain">${p.active ? '<span class="panel-status-active"> Active</span>' : '<span class="panel-status-pending"> Pending</span>'}</div></div>
            </div>
        `;
    } catch (e) {
        showToast('Failed to load: ' + e.message, 'error');
    }
}

load();
