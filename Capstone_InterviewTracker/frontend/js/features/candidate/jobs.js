
function normalizeDateValue(value) {
    if (!value || typeof value !== 'string') return '';
    if (/^\d{4}-\d{2}-\d{2}$/.test(value)) return value;
    const parts = value.split(/[-/]/);
    if (parts.length !== 3) return '';
    const [first, second, third] = parts;
    if (first.length === 4) {
        return `${first}-${second.padStart(2, '0')}-${third.padStart(2, '0')}`;
    }
    return `${third}-${second.padStart(2, '0')}-${first.padStart(2, '0')}`;
}
auth.requireRole('CANDIDATE');
document.getElementById('sidebar').innerHTML = buildSidebar('CANDIDATE', 'jobs');

let myApplication = null;
let allOpenJobs = [];

function readCandidateSignupDraft() {
    try {
        const raw = localStorage.getItem('candidateSignupDraft');
        return raw ? JSON.parse(raw) : null;
    } catch (e) {
        return null;
    }
}

function setFieldValue(id, value) {
    const element = document.getElementById(id);
    if (element && value !== undefined && value !== null) {
        element.value = value;
    }
}

function prefillApplyForm() {
    const signupDraft = readCandidateSignupDraft() || {};
    const source = myApplication || signupDraft;

    setFieldValue('applyName', source.fullName || localStorage.getItem('fullName') || '');
    setFieldValue('applyEmail', source.email || localStorage.getItem('userEmail') || '');
    setFieldValue('applyMobileCode', '+91');
    setFieldValue('applyMobile', source.mobileNumber || '');
    setFieldValue('applyDob', normalizeDateValue(source.dateOfBirth));

    function capMobileInput() {
        const mobileInput = document.getElementById('applyMobile');
        if (!mobileInput) return;
        mobileInput.addEventListener('input', () => {
            mobileInput.value = mobileInput.value.replace(/\D/g, '').slice(0, 10);
        });
    }
    setFieldValue('applyOrg', source.currentOrganization || '');
    setFieldValue('applyLocation', source.preferredLocation || '');
    setFieldValue('applyGender', source.gender || '');
    setFieldValue('applyTotalExp', source.totalExperience ?? '');
    setFieldValue('applyRelExp', source.relevantExperience ?? '');
    setFieldValue('applyCurrentCtc', source.currentCtc ?? '');
    setFieldValue('applyExpectedCtc', source.expectedCtc ?? '');
    setFieldValue('applyNotice', source.noticePeriod ?? '');
    setFieldValue('applySource', source.source || 'LinkedIn');
}

function shortText(text, max = 180) {
    const value = (text || '').trim();
    if (!value) return 'No description available.';
    return value.length > max ? value.slice(0, max) + '...' : value;
}

function openJobDetails(id) {
    const jd = allOpenJobs.find(item => item.id === id);
    if (!jd) return;
    showJdModal({
        jobTitle: jd.jobTitle,
        title: jd.jobTitle || 'Untitled Role',
        location: jd.location,
        jobType: jd.jobType,
        minExperience: jd.minExperience,
        maxExperience: jd.maxExperience,
        minSalary: jd.minSalary,
        maxSalary: jd.maxSalary,
        skills: jd.skills || [],
        jobDescription: jd.jobDescription,
        details: jd.jobDescription || 'No details provided.'
    });
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
        const applicationRes = await api.getMyProfile().catch(() => null);

        myApplication = applicationRes ? applicationRes.data : null;
    } catch (e) {
        myApplication = null;
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
            jobsEl.innerHTML = '<div class="job-empty-msg">No open roles from HR right now.</div>';
            return;
        }

        jobsEl.innerHTML = jobs.map(j => `
            <div class="job-row-card">
                <div class="job-main">
                    <div class="job-title">${j.jobTitle || 'Untitled Role'}</div>
                    <div class="job-meta"> ${j.location || 'N/A'} &nbsp;|&nbsp;  ${j.jobType || 'N/A'} &nbsp;|&nbsp;  ${j.minExperience ?? 0}-${j.maxExperience ?? 0} yrs &nbsp;|&nbsp;  ${j.minSalary ?? 0}-${j.maxSalary ?? 0} LPA</div>
                    <div class="job-desc">${shortText(j.jobDescription)}</div>
                    <div class="tag-wrap">${(j.skills || []).map(s => `<span class="tag">${s}</span>`).join('')}</div>
                </div>
                <div class="job-actions">
                    <button class="secondary-btn btn-compact" onclick="openJobDetails(${j.id})">View Job</button>
                    <button class="apply-btn" onclick="openApplyModal(${j.id}, '${j.jobTitle.replace(/'/g, "\\'")}')" ${locked ? 'disabled' : ''}>${locked ? 'Application Locked' : 'Apply Now'}</button>
                </div>
            </div>
        `).join('');
    } catch (e) {
        jobsEl.innerHTML = '<div class="job-error-msg">Failed to load open roles.</div>';
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

    document.getElementById('candidateApplyForm').reset();
    setFieldValue('applyMobileCode', '+91');
    prefillApplyForm();
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

    const resumeFile = document.getElementById('applyResume').files[0];
    if (!resumeFile) {
        errorMsg.textContent = 'Resume is required.';
        errorMsg.style.display = 'block';
        return;
    }

    const emailValue = getApplyFieldValue('applyEmail').trim();
    const mobileValue = getApplyFieldValue('applyMobile').trim();
    const dobValue = getApplyFieldValue('applyDob');
    const totalExperienceValue = parseFloat(getApplyFieldValue('applyTotalExp'));
    const relevantExperienceValue = parseFloat(getApplyFieldValue('applyRelExp'));
    const currentCtcValue = parseFloat(getApplyFieldValue('applyCurrentCtc'));
    const expectedCtcValue = parseFloat(getApplyFieldValue('applyExpectedCtc'));
    const noticePeriodValue = parseInt(getApplyFieldValue('applyNotice'));

    if ([totalExperienceValue, relevantExperienceValue, currentCtcValue, expectedCtcValue, noticePeriodValue].some(Number.isNaN)) {
        errorMsg.textContent = 'Please fill all numeric fields with valid values.';
        errorMsg.style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'Submit Application';
        return;
    }

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
        mobileCode: '+91',
        mobileNumber: mobileValue,
        dateOfBirth: dobValue,
        currentOrganization: getApplyFieldValue('applyOrg'),
        preferredLocation: getApplyFieldValue('applyLocation'),
        gender: getApplyFieldValue('applyGender'),
        totalExperience: totalExperienceValue,
        relevantExperience: relevantExperienceValue,
        currentCtc: currentCtcValue,
        expectedCtc: expectedCtcValue,
        noticePeriod: noticePeriodValue,
        source: getApplyFieldValue('applySource')
    };

    try {
        const res = await api.registerCandidate(payload);
        const candidateId = res.data.id;
        await api.uploadResume(candidateId, resumeFile);

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

// Setup event listeners for modal buttons
document.addEventListener('DOMContentLoaded', () => {
    const modalCloseBtn = document.getElementById('modalCloseBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const submitBtn = document.getElementById('submitBtn');

    if (modalCloseBtn) modalCloseBtn.addEventListener('click', closeApplyModal);
    if (cancelBtn) cancelBtn.addEventListener('click', closeApplyModal);
    if (submitBtn) submitBtn.addEventListener('click', submitApplication);
    capMobileInput();
});

window.openJobDetails = openJobDetails;
window.openApplyModal = openApplyModal;

async function initJobsPage() {
    await loadCandidateProfile();
    await loadAvailableJobs();
}

initJobsPage();
