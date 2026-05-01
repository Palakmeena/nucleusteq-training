auth.requireRole('CANDIDATE');
document.getElementById('sidebar').innerHTML = buildSidebar('CANDIDATE', 'jobs');

let myApplication = null;
let myLiveProfile = null;
let allOpenJobs = [];

function shortText(text, max = 180) {
    const value = (text || '').trim();
    if (!value) return 'No description available.';
    return value.length > max ? value.slice(0, max) + '...' : value;
}

function openJobDetails(id) {
    const jd = allOpenJobs.find(item => item.id === id);
    if (!jd) return;
    showJdModal({ title: jd.jobTitle || 'Untitled Role', details: jd.jobDescription || 'No details provided.' });
}

function canApplyNow() {
    return !myApplication || !myApplication.currentStage || myApplication.currentStage === 'REJECTED';
}

function getApplyFieldValue(id, fallback = '') {
    const element = document.getElementById(id);
    return element ? element.value : fallback;
}

async function loadCandidateProfile() {
    try {
        const [applicationRes, liveProfileRes] = await Promise.all([
            api.getMyProfile().catch(() => null),
            api.getMyLiveProfile().catch(() => null)
        ]);

        myApplication = applicationRes ? applicationRes.data : null;
        myLiveProfile = liveProfileRes ? liveProfileRes.data : null;
    } catch (e) {
        myApplication = null;
        myLiveProfile = null;
    }
}

async function loadAvailableJobs() {
    const jobsEl = document.getElementById('availableJobsList');
    const ruleTextEl = document.getElementById('applyRuleText');
    const locked = !canApplyNow();

    ruleTextEl.textContent = locked
        ? 'You already have an active application. You can apply again only after HR marks it Rejected.'
        : 'You can apply for one role now.';

    try {
        const res = await api.getAllPublicJds();
        const jobs = res.data || [];
        allOpenJobs = jobs;

        if (!jobs.length) {
            jobsEl.innerHTML = '<div style="text-align:center;color:#94a3b8;padding:28px;">No open roles from HR right now.</div>';
            return;
        }

        jobsEl.innerHTML = jobs.map(j => `
            <div class="job-row-card">
                <div style="flex:1;min-width:0;">
                    <div class="job-title">${j.jobTitle || 'Untitled Role'}</div>
                    <div class="job-meta"> ${j.location || 'N/A'} &nbsp;|&nbsp;  ${j.jobType || 'N/A'} &nbsp;|&nbsp;  ${j.minExperience ?? 0}-${j.maxExperience ?? 0} yrs &nbsp;|&nbsp;  ${j.minSalary ?? 0}-${j.maxSalary ?? 0} LPA</div>
                    <div class="job-desc">${shortText(j.jobDescription)}</div>
                    <div class="tag-wrap">${(j.skills || []).map(s => `<span class="tag">${s}</span>`).join('')}</div>
                </div>
                <div style="display:flex;align-items:center;justify-content:flex-end;gap:8px;flex-wrap:wrap;">
                    <button class="secondary-btn" style="padding:8px 14px;font-size:13px;" onclick="openJobDetails(${j.id})">View Job</button>
                    <button class="apply-btn" onclick="openApplyModal(${j.id}, '${j.jobTitle.replace(/'/g, "\\'")}')" ${locked ? 'disabled' : ''}>${locked ? 'Application Locked' : 'Apply Now'}</button>
                </div>
            </div>
        `).join('');
    } catch (e) {
        jobsEl.innerHTML = '<div style="text-align:center;color:#ef4444;padding:28px;">Failed to load open roles.</div>';
    }
}

function openApplyModal(jobId, jobTitle) {
    if (!canApplyNow()) {
        showToast('You already have an active application.', 'error');
        return;
    }

    document.getElementById('modalJobTitle').textContent = jobTitle;
    document.getElementById('applyJdId').value = jobId;
    document.getElementById('candidateApplyModal').style.display = 'flex';

    // Pre-fill if profile exists
    const profileToUse = myLiveProfile || myApplication;
    if (profileToUse) {
        document.getElementById('applyName').value = profileToUse.fullName || '';
        document.getElementById('applyEmail').value = profileToUse.email || '';
        document.getElementById('applyMobileCode').value = profileToUse.mobileCode || '+91';
        document.getElementById('applyMobile').value = profileToUse.mobileNumber || '';
        document.getElementById('applyDob').value = profileToUse.dateOfBirth || '';
        document.getElementById('applyOrg').value = profileToUse.currentOrganization || '';
        document.getElementById('applyLocation').value = profileToUse.preferredLocation || '';
        if (profileToUse.gender && document.getElementById('applyGender')) {
            document.getElementById('applyGender').value = profileToUse.gender;
        }
        document.getElementById('applyTotalExp').value = profileToUse.totalExperience || '';
        document.getElementById('applyRelExp').value = profileToUse.relevantExperience || '';
        document.getElementById('applyCurrentCtc').value = profileToUse.currentCtc || '';
        document.getElementById('applyExpectedCtc').value = profileToUse.expectedCtc || '';
        document.getElementById('applyNotice').value = profileToUse.noticePeriod || '';
    } else {
        document.getElementById('applyEmail').value = localStorage.getItem('userEmail') || '';
        document.getElementById('applyName').value = localStorage.getItem('userName') || '';
    }
}

function closeApplyModal() {
    document.getElementById('candidateApplyModal').style.display = 'none';
    document.getElementById('candidateApplyForm').reset();
    document.getElementById('applyErrorMsg').style.display = 'none';
    document.getElementById('applySuccessMsg').style.display = 'none';
}

async function submitApplication() {
    const form = document.getElementById('candidateApplyForm');
    const errorMsg = document.getElementById('applyErrorMsg');
    const successMsg = document.getElementById('applySuccessMsg');
    const btn = document.getElementById('submitBtn');

    if (!form.checkValidity()) {
        errorMsg.textContent = 'Please fill all mandatory fields.';
        errorMsg.style.display = 'block';
        return;
    }

    const emailValue = getApplyFieldValue('applyEmail').trim();
    const mobileValue = getApplyFieldValue('applyMobile').trim();
    const dobValue = getApplyFieldValue('applyDob');
    const totalExperienceValue = parseFloat(getApplyFieldValue('applyTotalExp'));
    const relevantExperienceValue = parseFloat(getApplyFieldValue('applyRelExp'));

    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(emailValue)) {
        errorMsg.textContent = 'Please provide a valid email (e.g. name@example.com).';
        errorMsg.style.display = 'block';
        return;
    }

    if (mobileValue.length !== 10 || isNaN(mobileValue)) {
        errorMsg.textContent = 'Mobile number must be exactly 10 digits.';
        errorMsg.style.display = 'block';
        return;
    }

    if (!getApplyFieldValue('applyGender')) {
        errorMsg.textContent = 'Gender is required.';
        errorMsg.style.display = 'block';
        return;
    }

    if (dobValue) {
        const birthDate = new Date(dobValue);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        if (age < 18) {
            errorMsg.textContent = 'Candidate must be at least 18 years old.';
            errorMsg.style.display = 'block';
            return;
        }
        if (birthDate.getFullYear() < 1950) {
            errorMsg.textContent = 'Please provide a valid Date of Birth (Year > 1950).';
            errorMsg.style.display = 'block';
            return;
        }
    }

    if (!isNaN(relevantExperienceValue) && !isNaN(totalExperienceValue) && relevantExperienceValue > totalExperienceValue) {
        errorMsg.textContent = 'Relevant experience cannot be greater than total experience.';
        errorMsg.style.display = 'block';
        return;
    }

    errorMsg.style.display = 'none';
    btn.disabled = true;
    btn.textContent = 'Submitting...';

    const payload = {
        jobDescriptionId: parseInt(getApplyFieldValue('applyJdId')),
        fullName: getApplyFieldValue('applyName'),
        email: emailValue,
        mobileCode: getApplyFieldValue('applyMobileCode'),
        mobileNumber: mobileValue,
        dateOfBirth: dobValue,
        currentOrganization: getApplyFieldValue('applyOrg'),
        preferredLocation: getApplyFieldValue('applyLocation'),
        gender: getApplyFieldValue('applyGender'),
        totalExperience: totalExperienceValue,
        relevantExperience: relevantExperienceValue,
        currentCtc: parseFloat(getApplyFieldValue('applyCurrentCtc')),
        expectedCtc: parseFloat(getApplyFieldValue('applyExpectedCtc')),
        noticePeriod: parseInt(getApplyFieldValue('applyNotice')),
        source: getApplyFieldValue('applySource')
    };

    try {
        const res = await api.registerCandidate(payload);
        const candidateId = res.data.id;
        const resumeFile = document.getElementById('applyResume').files[0];
        
        if (resumeFile) {
            await api.uploadResume(candidateId, resumeFile);
        }

        successMsg.style.display = 'block';
        setTimeout(() => {
            closeApplyModal();
            window.location.href = 'dashboard.html';
        }, 1500);
    } catch (err) {
        errorMsg.textContent = err.message;
        errorMsg.style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'Submit Application';
    }
}

window.openJobDetails = openJobDetails;
window.openApplyModal = openApplyModal;
window.closeApplyModal = closeApplyModal;
window.submitApplication = submitApplication;

async function initJobsPage() {
    await loadCandidateProfile();
    await loadAvailableJobs();
}

initJobsPage();
