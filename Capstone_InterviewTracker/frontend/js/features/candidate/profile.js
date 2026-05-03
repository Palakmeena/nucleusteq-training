auth.requireRole('CANDIDATE');
document.getElementById('sidebar').innerHTML = buildSidebar('CANDIDATE', 'profile');

let currentData = null;

async function loadProfile() {
    try {
        const res = await api.getMyProfile();
        currentData = res.data;
        renderView(currentData);
    } catch (e) {
        showToast('Error loading profile', 'error');
    }
}

function renderView(p) {
    // Hero
    document.getElementById('heroName').textContent = p.fullName || 'User';
    document.getElementById('heroEmail').textContent = p.email;
    document.getElementById('avatarInitials').textContent = (p.fullName || 'U').split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
    document.getElementById('applicationBadge').innerHTML = stageBadge(p.currentStage);

    // Grid
    document.getElementById('vName').textContent = p.fullName || '—';
    document.getElementById('vDob').textContent = p.dateOfBirth ? formatDate(p.dateOfBirth) : '—';
    document.getElementById('vMobile').textContent = (p.mobileCode || '') + ' ' + (p.mobileNumber || '—');
    document.getElementById('vLocation').textContent = p.preferredLocation || '—';
    document.getElementById('vGender').textContent = p.gender || '—';
    document.getElementById('vOrg').textContent = p.currentOrganization || '—';
    document.getElementById('vTotalExp').textContent = (p.totalExperience || 0) + ' years';
    document.getElementById('vRelExp').textContent = (p.relevantExperience || 0) + ' years';
    document.getElementById('vNotice').textContent = (p.noticePeriod || 0) + ' days';
    document.getElementById('vCurrentCtc').textContent = (p.currentCtc || 0) + ' LPA';
    document.getElementById('vExpectedCtc').textContent = (p.expectedCtc || 0) + ' LPA';
    document.getElementById('vJobTitle').textContent = p.jobTitle || 'N/A';
    document.getElementById('vResume').innerHTML = p.resumePath ? `<a href="${p.resumePath}" target="_blank" class="profile-resume-link"> View Resume</a>` : '<span class="profile-resume-empty">Not uploaded</span>';
}

function openEditModal() {
    if (!currentData) return;
    document.getElementById('eName').value = currentData.fullName || '';
    document.getElementById('eDob').value = currentData.dateOfBirth || '';
    document.getElementById('eMobileCode').value = currentData.mobileCode || '';
    document.getElementById('eMobileNumber').value = currentData.mobileNumber || '';
    document.getElementById('eOrg').value = currentData.currentOrganization || '';
    document.getElementById('eLocation').value = currentData.preferredLocation || '';
    document.getElementById('eGender').value = currentData.gender || '';
    document.getElementById('eTotalExp').value = currentData.totalExperience || '';
    document.getElementById('eRelExp').value = currentData.relevantExperience || '';
    document.getElementById('eCurrentCtc').value = currentData.currentCtc || '';
    document.getElementById('eExpectedCtc').value = currentData.expectedCtc || '';
    document.getElementById('eNotice').value = currentData.noticePeriod || '';

    document.getElementById('editModal').classList.add('active');
}

function closeEditModal() {
    document.getElementById('editModal').classList.remove('active');
}

async function saveChanges() {
    const btn = document.getElementById('saveBtn');
    btn.disabled = true;
    btn.textContent = 'Saving...';

    const body = {
        fullName: document.getElementById('eName').value,
        dateOfBirth: document.getElementById('eDob').value,
        mobileCode: document.getElementById('eMobileCode').value,
        mobileNumber: document.getElementById('eMobileNumber').value,
        currentOrganization: document.getElementById('eOrg').value,
        preferredLocation: document.getElementById('eLocation').value,
        gender: document.getElementById('eGender').value,
        totalExperience: document.getElementById('eTotalExp').value,
        relevantExperience: document.getElementById('eRelExp').value,
        currentCtc: document.getElementById('eCurrentCtc').value,
        expectedCtc: document.getElementById('eExpectedCtc').value,
        noticePeriod: document.getElementById('eNotice').value
    };

    try {
        // Strict Validation
        if (body.mobileNumber.length !== 10) {
            showToast('Mobile number must be exactly 10 digits.', 'error');
            btn.disabled = false;
            btn.textContent = 'Save Changes';
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
                btn.textContent = 'Save Changes';
                return;
            }
            if (birthDate.getFullYear() < 1950) {
                showToast('Please provide a valid Date of Birth (Year > 1950).', 'error');
                btn.disabled = false;
                btn.textContent = 'Save Changes';
                return;
            }
        }

        if (parseFloat(body.relevantExperience) > parseFloat(body.totalExperience)) {
            showToast('Relevant experience cannot be greater than total experience.', 'error');
            btn.disabled = false;
            btn.textContent = 'Save Changes';
            return;
        }

        await api.updateMyProfile(body);
        showToast('Profile updated successfully', 'success');
        closeEditModal();
        loadProfile();
    } catch (e) {
        showToast(e.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Save Changes';
    }
}

async function uploadResume(input) {
    if (!input.files || !input.files[0]) return;
    const file = input.files[0];
    const status = document.getElementById('uploadStatus');
    const btn = document.getElementById('uploadBtn');

    try {
        btn.disabled = true;
        status.textContent = 'Uploading to Drive...';
        await api.uploadProfileResume(file);
        showToast('Resume uploaded successfully', 'success');
        status.textContent = ' Uploaded: ' + file.name;
    } catch (e) {
        showToast(e.message, 'error');
        status.textContent = ' Upload failed';
    } finally {
        btn.disabled = false;
    }
}

// Needed for existing inline onclick handlers in HTML
window.openEditModal = openEditModal;
window.closeEditModal = closeEditModal;
window.saveChanges = saveChanges;
window.uploadResume = uploadResume;

loadProfile();
