auth.requireRole('HR');
document.getElementById('sidebar').innerHTML = buildSidebar('HR', 'panels');

async function loadPanels() {
    try {
        const res = await api.getAllPanelMembers();
        const panels = res.data || [];
        const container = document.getElementById('panelList');
        container.innerHTML = panels.length ? panels.map(p => `
            <div class="panel-card">
                <div style="display:flex;align-items:center;gap:14px;">
                    <div style="width:44px;height:44px;border-radius:50%;background:#eef2ff;color:#4f46e5;display:flex;align-items:center;justify-content:center;font-weight:700;">
                        ${p.fullName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                    </div>
                    <div class="panel-info">
                        <h3>${p.fullName}</h3>
                        <p>${p.email} &nbsp;|&nbsp; ${p.organization} &nbsp;|&nbsp; ${p.designation}</p>
                    </div>
                </div>
                <div style="display:flex;align-items:center;gap:12px;">
                    <span class="jd-badge" style="${p.active ? 'background:#dcfce7;color:#16a34a;' : 'background:#fef3c7;color:#d97706;'}">${p.active ? ' Active' : ' Pending'}</span>
                    <div style="display:flex;gap:8px;">
                        <button onclick="openEditModal(${JSON.stringify(p).replace(/"/g, '&quot;')})" style="background:none;border:none;cursor:pointer;color:#4f46e5;font-size:16px;" title="Edit">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                        </button>
                        <button onclick="deletePanel(${p.id})" style="background:none;border:none;cursor:pointer;color:#ef4444;font-size:16px;" title="Delete">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                        </button>
                    </div>
                </div>
            </div>
        `).join('') : '<p style="text-align:center;padding:40px;color:#94a3b8;">No panel members yet. Add your first interviewer!</p>';
    } catch (e) {
        showToast('Failed to load panel members: ' + e.message, 'error');
    }
}

let currentEditingId = null;

function openEditModal(p) {
    currentEditingId = p.id;
    document.getElementById('editPanelModal').classList.add('active');
    document.getElementById('eFullName').value = p.fullName;
    document.getElementById('eEmail').value = p.email;

    let mob = p.mobileNumber || '';
    if (mob.startsWith('+91')) {
        mob = mob.substring(3);
    } else if (mob.startsWith('+')) {
        const match = mob.match(/^(\+\d{1,3})(.*)$/);
        if (match) {
            mob = match[2];
        }
    }
    document.getElementById('eMobile').value = mob;

    document.getElementById('eOrg').value = p.organization;
    document.getElementById('eDesignation').value = p.designation;
}

async function updatePanel() {
    const body = {
        fullName: document.getElementById('eFullName').value,
        mobileNumber: '+91' + document.getElementById('eMobile').value,
        organization: document.getElementById('eOrg').value,
        designation: document.getElementById('eDesignation').value
    };
    try {
        await api.updatePanelMember(currentEditingId, body);
        showToast('Panel member updated successfully', 'success');
        document.getElementById('editPanelModal').classList.remove('active');
        loadPanels();
    } catch (e) {
        showToast('Update failed: ' + e.message, 'error');
    }
}

async function deletePanel(id) {
    if (!confirm('Are you sure you want to remove this panel member? This action cannot be undone.')) return;
    try {
        await api.deletePanelMember(id);
        showToast('Panel member removed', 'success');
        loadPanels();
    } catch (e) {
        showToast('Delete failed: ' + e.message, 'error');
    }
}

async function createPanel() {
    const pMobileVal = document.getElementById('pMobile').value;
    const body = {
        fullName: document.getElementById('pFullName').value,
        email: document.getElementById('pEmail').value,
        mobileNumber: pMobileVal ? '+91' + pMobileVal : '',
        organization: document.getElementById('pOrg').value,
        designation: document.getElementById('pDesignation').value
    };
    if (!body.fullName || !body.email || !body.mobileNumber || !body.organization || !body.designation) {
        document.getElementById('panelFormError').textContent = 'Please fill all fields';
        document.getElementById('panelFormError').style.display = 'block';
        return;
    }

    document.getElementById('panelFormError').style.display = 'none';
    try {
        const res = await api.createPanelMember(body);
        const panel = res.data || {};
        let shouldCloseModal = true;

        if (panel.activationEmailSent) {
            showToast('Panel member created! Activation link sent.', 'success');
        } else if (panel.activationLink) {
            document.getElementById('panelFormError').innerHTML =
                'Email delivery failed. Share this activation link manually:<br><a href="'
                + panel.activationLink
                + '" target="_blank" style="color:#1d4ed8;word-break:break-all;">'
                + panel.activationLink
                + '</a>';
            document.getElementById('panelFormError').style.display = 'block';
            showToast('Activation email failed. Manual link generated.', 'error');
            shouldCloseModal = false;
        } else {
            showToast('Panel member created. Activation may require manual link sharing.', 'default');
        }

        if (shouldCloseModal) {
            document.getElementById('addPanelModal').classList.remove('active');
        }
        loadPanels();
    } catch (e) {
        document.getElementById('panelFormError').textContent = e.message;
        document.getElementById('panelFormError').style.display = 'block';
    }
}

window.openEditModal = openEditModal;
window.updatePanel = updatePanel;
window.deletePanel = deletePanel;
window.createPanel = createPanel;

loadPanels();
