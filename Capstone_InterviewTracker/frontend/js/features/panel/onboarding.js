auth.requireRole('HR');
document.getElementById('sidebar').innerHTML = buildSidebar('HR', 'panels');

let allPanels = [];
let currentPanelSearchQuery = '';
const panelSearchInput = document.getElementById('panelSearchInput');
const panelCountLabel = document.getElementById('panelCountLabel');

function panelNormalizeText(value) {
    return String(value || '').toLowerCase();
}

function panelMatchesSearch(panel, query) {
    if (!query) return true;
    const haystack = [
        panel.fullName,
        panel.email,
        panel.organization,
        panel.designation
    ].map(panelNormalizeText).join(' ');
    return haystack.includes(query);
}

function renderPanels() {
    const container = document.getElementById('panelList');
    const query = panelNormalizeText(currentPanelSearchQuery.trim());
    const filteredPanels = allPanels.filter(panel => panelMatchesSearch(panel, query));

    if (panelCountLabel) {
        panelCountLabel.textContent = query
            ? `${filteredPanels.length} of ${allPanels.length} panel members shown`
            : `${allPanels.length} panel members`;
    }

    container.innerHTML = filteredPanels.length ? filteredPanels.map(p => `
            <div class="panel-card">
                <div class="panel-user">
                    <div class="panel-avatar">
                        ${p.fullName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                    </div>
                    <div class="panel-info">
                        <h3>${p.fullName}</h3>
                        <p>${p.email} &nbsp;|&nbsp; ${p.organization} &nbsp;|&nbsp; ${p.designation}</p>
                    </div>
                </div>
                <div class="panel-actions">
                    <span class="jd-badge ${p.active ? 'panel-badge-active' : 'panel-badge-pending'}">${p.active ? ' Active' : ' Pending'}</span>
                    <div class="panel-action-group">
                        <button class="panel-action-btn" onclick="openEditModal(${JSON.stringify(p).replace(/"/g, '&quot;')})" title="Edit">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                        </button>
                        <button class="panel-action-trash" onclick="deletePanel(${p.id})" title="Delete">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                        </button>
                    </div>
                </div>
            </div>
        `).join('') : (query
            ? '<div class="panel-empty">No panel members match your search.</div>'
            : '<div class="panel-empty">No panel members yet. Add your first interviewer!</div>');
}

async function loadPanels() {
    try {
        const res = await api.getAllPanelMembers();
        allPanels = res.data || [];
        renderPanels();
    } catch (e) {
        showToast('Failed to load panel members: ' + e.message, 'error');
        const container = document.getElementById('panelList');
        if (container) container.innerHTML = '<div class="panel-loading-state">Failed to load panel members.</div>';
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
    const emailVal = document.getElementById('eEmail').value;
    if (!emailVal) {
        showToast('Update failed: Email is required', 'error');
        return;
    }

    const mobileVal = document.getElementById('eMobile').value || '';
    const body = {
        fullName: document.getElementById('eFullName').value,
        email: emailVal,
        mobileNumber: mobileVal ? '+91' + mobileVal : '',
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
    const createBtn = document.querySelector('.panel-primary-auto');
    const originalText = createBtn.textContent;
    createBtn.textContent = 'Creating...';

    try {
        const res = await api.createPanelMember(body);
        const panel = res.data || {};
        let shouldCloseModal = true;

        if (panel.activationEmailSent) {
            showToast('Panel member created! Activation link sent.', 'success');
        } else if (panel.activationLink) {
            document.getElementById('panelFormError').innerHTML =
                'Email delivery failed. Share this activation link manually:<br><a href="' +
                panel.activationLink +
                '" target="_blank" class="panel-activation-link">' +
                panel.activationLink +
                '</a>';
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
    } finally {
        createBtn.textContent = originalText;
    }
}

window.openEditModal = openEditModal;
window.updatePanel = updatePanel;
window.deletePanel = deletePanel;
window.createPanel = createPanel;

if (panelSearchInput) {
    panelSearchInput.addEventListener('input', () => {
        currentPanelSearchQuery = panelSearchInput.value;
        renderPanels();
    });
}

loadPanels();
