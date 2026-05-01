auth.requireRole('HR');
document.getElementById('sidebar').innerHTML = buildSidebar('HR', 'candidates');

// Add stage listener to hide/show panel search
document.getElementById('schedStage').addEventListener('change', function (e) {
    const panelGroup = document.getElementById('panelGroup');
    if (e.target.value === 'HR_ROUND') {
        panelGroup.style.display = 'none';
        selectedPanels = [];
        updateSelectedPanelsDisplay();
    } else {
        panelGroup.style.display = 'block';
    }
});

let allCandidates = [];

async function loadCandidates() {
    try {
        const res = await api.getAllCandidates();
        allCandidates = res.data || [];
        renderCandidates(allCandidates);
    } catch (e) {
        showToast('Failed to load candidates: ' + e.message, 'error');
    }
}

function renderCandidates(list) {
    document.getElementById('countLabel').textContent = `${list.length} candidate${list.length !== 1 ? 's' : ''}`;
    const tbody = document.getElementById('candidateTableBody');
    tbody.innerHTML = list.length ? list.map(c => `
        <tr>
            <td>
                <div style="display:flex;align-items:center;gap:10px;">
                    <div style="width:32px;height:32px;border-radius:50%;background:#eef2ff;color:#4f46e5;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:12px;flex-shrink:0;">
                        ${(c.fullName || 'CN').split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)}
                    </div>
                    <div>
                        <div style="font-weight:500;">${c.fullName}</div>
                        <div style="font-size:12px;color:#64748b;">${c.email}</div>
                    </div>
                </div>
            </td>
            <td style="font-size:13px;">${c.jobTitle || '—'}</td>
            <td style="font-size:13px;">${c.totalExperience} yrs</td>
            <td>
                <select class="filter-btn" style="padding:6px 10px; font-size:12px; border-radius:6px; min-width:120px;" 
                        onchange="updateStage(${c.id}, this.value)">
                    <option value="PROFILING" ${c.currentStage === 'PROFILING' ? 'selected' : ''}>Profiling</option>
                    <option value="SCREENING" ${c.currentStage === 'SCREENING' ? 'selected' : ''}>Screening</option>
                    <option value="L1_TECHNICAL" ${c.currentStage === 'L1_TECHNICAL' ? 'selected' : ''}>L1 Technical</option>
                    <option value="L2_TECHNICAL" ${c.currentStage === 'L2_TECHNICAL' ? 'selected' : ''}>L2 Technical</option>
                    <option value="HR_ROUND" ${c.currentStage === 'HR_ROUND' ? 'selected' : ''}>HR Round</option>
                    <option value="SELECTED" ${c.currentStage === 'SELECTED' ? 'selected' : ''}>Selected</option>
                    <option value="REJECTED" ${c.currentStage === 'REJECTED' ? 'selected' : ''}>Rejected</option>
                </select>
            </td>
            <td style="font-size:13px;color:#64748b;">${formatDate(c.createdAt)}</td>
            <td>
                <button onclick="openScheduleModal(${c.id}, '${c.fullName.replace(/'/g, "\\'")}')" style="font-size:13px;color:#4f46e5;background:none;border:1px solid #4f46e5;border-radius:6px;padding:4px 10px;cursor:pointer;margin-right:6px;">Schedule</button>
                <span style="font-size:13px;color:#64748b;cursor:pointer;margin-right:12px;" onclick="viewCandidate(${c.id})">View</span>
                <button onclick="deleteCandidate(${c.id})" style="background:none;border:none;color:#ef4444;cursor:pointer;padding:0;font-size:16px;" title="Delete Candidate"><i class="fas fa-trash-alt"></i></button>
            </td>
        </tr>
    `).join('') : '<tr><td colspan="6" style="text-align:center;padding:40px;color:#94a3b8;">No candidates found</td></tr>';
}

async function updateStage(id, newStage) {
    try {
        await api.updateCandidateStage(id, newStage);
        showToast('Candidate stage updated successfully', 'success');
        const cand = allCandidates.find(c => c.id === id);
        if (cand) cand.currentStage = newStage;
    } catch (e) {
        showToast('Failed to update stage: ' + e.message, 'error');
        loadCandidates();
    }
}

async function deleteCandidate(id) {
    if (!confirm('Are you sure you want to delete this candidate? This will also remove all their interview history and account.')) return;
    try {
        await api.deleteCandidate(id);
        showToast('Candidate deleted successfully', 'success');
        loadCandidates();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

function filterCandidates() {
    const search = document.getElementById('searchInput').value.toLowerCase();
    const stage = document.getElementById('stageFilter').value;
    let filtered = allCandidates;
    if (stage) filtered = filtered.filter(c => c.currentStage === stage);
    if (search) filtered = filtered.filter(c =>
        c.fullName.toLowerCase().includes(search) ||
        c.email.toLowerCase().includes(search)
    );
    renderCandidates(filtered);
}

function viewCandidate(id) {
    const c = allCandidates.find(item => item.id === id);
    if (!c) return;
    document.getElementById('viewCandidateModal').classList.add('active');
    document.getElementById('candidateName').textContent = c.fullName;
    document.getElementById('candidateEmail').textContent = c.email;
    document.getElementById('candidateMobile').textContent = (c.mobileCode || '') + ' ' + (c.mobileNumber || '—');
    document.getElementById('candidateDob').textContent = c.dateOfBirth ? formatDate(c.dateOfBirth) : '—';
    document.getElementById('candidateGender').textContent = c.gender || '—';
    document.getElementById('candidateOrg').textContent = c.currentOrganization || '—';
    document.getElementById('candidateLocation').textContent = c.preferredLocation || '—';
    document.getElementById('candidateTotalExp').textContent = (c.totalExperience || 0) + ' years';
    document.getElementById('candidateRelExp').textContent = (c.relevantExperience || 0) + ' years';
    document.getElementById('candidateCurrentCtc').textContent = (c.currentCtc || '—') + ' LPA';
    document.getElementById('candidateExpectedCtc').textContent = (c.expectedCtc || '—') + ' LPA';
    document.getElementById('candidateNotice').textContent = (c.noticePeriod || '—') + ' days';
    document.getElementById('candidateSource').textContent = c.source || '—';
    document.getElementById('candidateStage').innerHTML = stageBadge(c.currentStage);
    document.getElementById('candidateJobTitle').textContent = c.jobTitle || '—';
    document.getElementById('candidateAppliedOn').textContent = formatDate(c.createdAt) || '—';
    const resumeLink = document.getElementById('candidateResumeLink');
    const noResumeMsg = document.getElementById('noResumeMsg');
    if (c.resumePath) {
        resumeLink.href = c.resumePath;
        resumeLink.style.display = 'inline-flex';
        noResumeMsg.style.display = 'none';
    } else {
        resumeLink.style.display = 'none';
        noResumeMsg.style.display = 'block';
    }
}

function closeViewModal() {
    document.getElementById('viewCandidateModal').classList.remove('active');
}

function openAddModal() {
    loadActiveJds();
    document.getElementById('addCandidateModal').classList.add('active');
}

function closeAddModal() {
    document.getElementById('addCandidateModal').classList.remove('active');
    document.getElementById('addCandidateForm').reset();
}

async function loadActiveJds() {
    try {
        const res = await api.getAllJdsForHr();
        const jds = (res.data || []).filter(j => j.active);
        const select = document.getElementById('jdSelect');
        select.innerHTML = '<option value="">Select Job Role</option>' +
            jds.map(j => `<option value="${j.id}">${j.jobTitle}</option>`).join('');
    } catch (e) {
        showToast('Failed to load jobs', 'error');
    }
}

document.getElementById('addCandidateForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    btn.disabled = true;
    btn.textContent = 'Creating...';
    const body = {
        fullName: document.getElementById('addFullName').value,
        email: document.getElementById('addEmail').value,
        mobileCode: '+91',
        mobileNumber: document.getElementById('addMobile').value,
        jobDescriptionId: parseInt(document.getElementById('jdSelect').value),
        totalExperience: parseFloat(document.getElementById('addTotalExp').value),
        relevantExperience: parseFloat(document.getElementById('addRelExp').value),
        currentCtc: parseFloat(document.getElementById('addCurrentCtc').value),
        expectedCtc: parseFloat(document.getElementById('addExpectedCtc').value),
        noticePeriod: parseInt(document.getElementById('addNotice').value),
        currentOrganization: document.getElementById('addOrg').value,
        preferredLocation: document.getElementById('addLocation').value,
        gender: document.getElementById('addGender').value,
        dateOfBirth: document.getElementById('addDob').value,
        source: 'Manual'
    };

    // Strict Validation
    if (body.totalExperience < 0 || body.relevantExperience < 0 || body.currentCtc < 0 || body.expectedCtc < 0 || body.noticePeriod < 0) {
        showToast('Experience, CTC, and Notice Period cannot be negative.', 'error');
        btn.disabled = false;
        btn.textContent = 'Create Candidate';
        return;
    }

    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(body.email)) {
        showToast('Please provide a valid email (e.g. name@example.com).', 'error');
        btn.disabled = false;
        btn.textContent = 'Create Candidate';
        return;
    }

    if (body.mobileNumber.length !== 10 || isNaN(body.mobileNumber)) {
        showToast('Mobile number must be exactly 10 digits.', 'error');
        btn.disabled = false;
        btn.textContent = 'Create Candidate';
        return;
    }

    if (body.dateOfBirth) {
        const birthDate = new Date(body.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        if (age < 18) {
            showToast('Candidate must be at least 18 years old.', 'error');
            btn.disabled = false;
            btn.textContent = 'Create Candidate';
            return;
        }
        if (birthDate.getFullYear() < 1950) {
            showToast('Please provide a valid Date of Birth (Year > 1950).', 'error');
            btn.disabled = false;
            btn.textContent = 'Create Candidate';
            return;
        }
    }

    if (body.relevantExperience > body.totalExperience) {
        showToast('Relevant experience cannot be greater than total experience.', 'error');
        btn.disabled = false;
        btn.textContent = 'Create Candidate';
        return;
    }
    try {
        const created = await api.createCandidateByHr(body);
        const resumeFile = document.getElementById('addResume').files[0];
        const candidateId = created?.data?.id;
        if (resumeFile && candidateId) {
            await api.uploadResume(candidateId, resumeFile);
        }
        showToast('Candidate created successfully', 'success');
        closeAddModal();
        loadCandidates();
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Create Candidate';
    }
});

let allPanels = [];
let selectedPanels = [];
let currentSchedulingCandidateId = null;

async function loadPanels() {
    try {
        const pRes = await api.getAllPanelMembers();
        allPanels = pRes.data || [];
    } catch (e) {
        console.error('Failed to load panels', e);
    }
}

function openScheduleModal(id) {
    currentSchedulingCandidateId = id;
    document.getElementById('scheduleModal').classList.add('active');
    selectedPanels = [];
    updateSelectedPanelsDisplay();
    document.getElementById('panelSearchInput').value = '';
    document.getElementById('panelOptions').innerHTML = '<div style="color:#94a3b8;font-size:13px;padding:20px;text-align:center;">Type to search for panel members...</div>';
}

function closeScheduleModal() {
    document.getElementById('scheduleModal').classList.remove('active');
    document.getElementById('scheduleForm').reset();
    document.getElementById('schedError').style.display = 'none';
}

function filterPanels() {
    const searchTerm = document.getElementById('panelSearchInput').value.toLowerCase().trim();
    const container = document.getElementById('panelOptions');
    if (!searchTerm) {
        container.innerHTML = '<div style="color:#94a3b8;font-size:13px;padding:20px;text-align:center;">Type to search for panel members...</div>';
        return;
    }
    const filtered = allPanels.filter(p => p.fullName.toLowerCase().startsWith(searchTerm));
    if (filtered.length === 0) {
        container.innerHTML = '<div style="color:#94a3b8;font-size:13px;padding:20px;text-align:center;">No panel members found</div>';
        return;
    }
    container.innerHTML = filtered.slice(0, 3).map(p => `
        <div class="panel-option" onclick="togglePanel(${p.id})" ${selectedPanels.includes(p.id) ? 'style="border-color:#4f46e5;background:#eef2ff;"' : ''}>
            <input type="checkbox" ${selectedPanels.includes(p.id) ? 'checked' : ''}>
            <div>
                <div style="font-size:14px;font-weight:500;">${p.fullName}</div>
                <div style="font-size:12px;color:#64748b;">${p.designation} — ${p.organization}</div>
            </div>
        </div>
    `).join('');
}

function togglePanel(id) {
    const idx = selectedPanels.indexOf(id);
    if (idx > -1) selectedPanels.splice(idx, 1);
    else {
        if (selectedPanels.length >= 2) {
            showToast('Max 2 panels allowed', 'error');
            filterPanels();
            return;
        }
        selectedPanels.push(id);
    }
    updateSelectedPanelsDisplay();
    filterPanels();
}

function updateSelectedPanelsDisplay() {
    const display = document.getElementById('selectedPanelsDisplay');
    const list = document.getElementById('selectedList');
    if (selectedPanels.length === 0) {
        display.style.display = 'none';
        return;
    }
    display.style.display = 'block';
    list.innerHTML = selectedPanels.map(pid => {
        const p = allPanels.find(x => x.id === pid);
        return `<div style="background:#fff;padding:6px 10px;border-radius:6px;display:flex;justify-content:space-between;margin-bottom:4px;font-size:13px;border:1px solid #e2e8f0;">
            <span>${p.fullName}</span>
            <button onclick="togglePanel(${pid})" style="background:none;border:none;color:#ef4444;cursor:pointer;">×</button>
        </div>`;
    }).join('');
}

async function scheduleInterview() {
    const errDiv = document.getElementById('schedError');
    errDiv.style.display = 'none';

    const saveBtn = document.getElementById('saveBtn');
    saveBtn.disabled = true;
    saveBtn.textContent = 'Scheduling...';

    const h = document.getElementById('schedHour').value;
    const m = document.getElementById('schedMinute').value;
    const ap = document.getElementById('schedAmPm').value;
    let time24 = '';
    if (h && m) {
        let hourNum = parseInt(h, 10);
        if (ap === 'PM' && hourNum !== 12) hourNum += 12;
        if (ap === 'AM' && hourNum === 12) hourNum = 0;
        time24 = `${hourNum.toString().padStart(2, '0')}:${m}`;
    }

    const body = {
        candidateId: currentSchedulingCandidateId,
        interviewStage: document.getElementById('schedStage').value,
        interviewDate: document.getElementById('schedDate').value,
        interviewTime: time24,
        meetingLink: document.getElementById('schedLink').value,
        focusAreas: document.getElementById('schedFocus').value,
        panelMemberIds: selectedPanels
    };
    const isHrRound = body.interviewStage === 'HR_ROUND';
    if (!body.interviewStage || !body.interviewDate || !body.interviewTime || (!isHrRound && !selectedPanels.length)) {
        errDiv.textContent = isHrRound ? 'Please fill all required fields' : 'Please fill all required fields and select at least one panel member';
        errDiv.style.display = 'block';
        saveBtn.disabled = false;
        saveBtn.textContent = 'Schedule Now';
        return;
    }
    try {
        await api.scheduleInterview(body);
        showToast('Interview scheduled successfully!', 'success');
        closeScheduleModal();
        loadCandidates();
    } catch (e) {
        errDiv.textContent = e.message;
        errDiv.style.display = 'block';
    } finally {
        saveBtn.disabled = false;
        saveBtn.textContent = 'Schedule Now';
    }
}

loadCandidates();
loadPanels();

// Expose functions for inline handlers in the HTML
window.openAddModal = openAddModal;
window.closeAddModal = closeAddModal;
window.filterCandidates = filterCandidates;
window.updateStage = updateStage;
window.viewCandidate = viewCandidate;
window.closeViewModal = closeViewModal;
window.openScheduleModal = openScheduleModal;
window.closeScheduleModal = closeScheduleModal;
window.togglePanel = togglePanel;
window.filterPanels = filterPanels;
window.scheduleInterview = scheduleInterview;
window.deleteCandidate = deleteCandidate;
