auth.requireRole('PANEL');
document.getElementById('sidebar').innerHTML = buildSidebar('PANEL', 'settings');

async function load() {
    try {
        const res = await api.getMyPanelProfile();
        const p = res.data;
        document.getElementById('profileDetails').innerHTML = `
            <div style="display:grid;gap:16px;">
                <div><div style="font-size:11px;color:#94a3b8;text-transform:uppercase;font-weight:600;margin-bottom:4px;">Full Name</div><div style="font-size:14px;font-weight:500;">${p.fullName}</div></div>
                <div><div style="font-size:11px;color:#94a3b8;text-transform:uppercase;font-weight:600;margin-bottom:4px;">Email</div><div style="font-size:14px;">${p.email}</div></div>
                <div><div style="font-size:11px;color:#94a3b8;text-transform:uppercase;font-weight:600;margin-bottom:4px;">Organization</div><div style="font-size:14px;">${p.organization}</div></div>
                <div><div style="font-size:11px;color:#94a3b8;text-transform:uppercase;font-weight:600;margin-bottom:4px;">Designation</div><div style="font-size:14px;">${p.designation}</div></div>
                <div><div style="font-size:11px;color:#94a3b8;text-transform:uppercase;font-weight:600;margin-bottom:4px;">Status</div><div style="font-size:14px;">${p.active ? '<span style="color:#10b981;"> Active</span>' : '<span style="color:#f59e0b;"> Pending</span>'}</div></div>
            </div>
        `;
    } catch (e) {
        showToast('Failed to load: ' + e.message, 'error');
    }
}

load();
