auth.requireRole('CANDIDATE');
document.getElementById('sidebar').innerHTML = buildSidebar('CANDIDATE', 'settings');

let currentProfile = null;

async function loadProfile() {
    try {
        const res = await api.getMyProfile();
        currentProfile = res.data;
        if (currentProfile) {
            document.getElementById('fullName').value = currentProfile.fullName || '';
            document.getElementById('dateOfBirth').value = currentProfile.dateOfBirth || '';
            document.getElementById('mobileCode').value = currentProfile.mobileCode || '';
            document.getElementById('mobileNumber').value = currentProfile.mobileNumber || '';
            document.getElementById('currentOrganization').value = currentProfile.currentOrganization || '';
            document.getElementById('preferredLocation').value = currentProfile.preferredLocation || '';
            document.getElementById('totalExperience').value = currentProfile.totalExperience || '';
            document.getElementById('relevantExperience').value = currentProfile.relevantExperience || '';

            const status = document.getElementById('resumeStatus');
            if (currentProfile.resumePath) {
                status.innerHTML = ` Resume is uploaded. <a href="${currentProfile.resumePath}" target="_blank" class="settings-resume-link">View Current</a>`;
            } else {
                status.innerHTML = ' No resume uploaded yet.';
            }
        }
    } catch (e) {
        showToast('Failed to load profile: ' + e.message, 'error');
    }
}

async function saveProfile() {
    const btn = document.getElementById('saveBtn');
    btn.disabled = true;
    btn.textContent = 'Saving...';

    const data = {
        fullName: document.getElementById('fullName').value,
        dateOfBirth: document.getElementById('dateOfBirth').value,
        mobileCode: document.getElementById('mobileCode').value,
        mobileNumber: document.getElementById('mobileNumber').value,
        currentOrganization: document.getElementById('currentOrganization').value,
        preferredLocation: document.getElementById('preferredLocation').value,
        totalExperience: document.getElementById('totalExperience').value,
        relevantExperience: document.getElementById('relevantExperience').value
    };

    try {
        await api.updateMyProfile(data);
        showToast('Profile updated successfully', 'success');
    } catch (e) {
        showToast('Update failed: ' + e.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Save Changes';
    }
}

async function handleFileSelect(input) {
    if (!input.files || !input.files[0]) return;
    const file = input.files[0];

    if (file.type !== 'application/pdf') {
        showToast('Only PDF files are allowed', 'error');
        return;
    }

    const uploadBtn = document.getElementById('uploadBtn');
    const status = document.getElementById('resumeStatus');

    try {
        uploadBtn.disabled = true;
        uploadBtn.textContent = 'Uploading to Drive...';
        status.textContent = 'Uploading... Please wait.';

        await api.uploadProfileResume(file);

        showToast('Resume uploaded successfully to Google Drive', 'success');
        loadProfile();
    } catch (e) {
        showToast('Upload failed: ' + e.message, 'error');
    } finally {
        uploadBtn.disabled = false;
        uploadBtn.textContent = 'Choose New Resume (PDF)';
    }
}

window.saveProfile = saveProfile;
window.handleFileSelect = handleFileSelect;

loadProfile();
