document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    const authSection = document.getElementById('authSection');

    if (token) {
        let dashboardLink = '../candidate/dashboard.html';
        if (role === 'HR') dashboardLink = '../hr/pipeline.html';
        else if (role === 'PANEL') dashboardLink = '../panel/interview-dashboard.html';
        authSection.innerHTML = `
            <a href="${dashboardLink}" class="login-btn jobs-dashboard-link">Dashboard</a>
            <button class="signup-btn" onclick="handleAuth()">Sign Out</button>
        `;
    }

    const jdListEl = document.getElementById('jdList');
    const searchInput = document.getElementById('searchInput');
    let allJds = [];

    api.getAllPublicJds()
        .then(result => {
            allJds = result.data || [];
            renderJds(allJds);
            checkAutoApply(allJds);
        })
        .catch(err => {
            jdListEl.innerHTML = `<div class="jobs-error-empty">${err.message || 'Error loading job descriptions'}</div>`;
        });

    searchInput.addEventListener('input', e => {
        const term = e.target.value.toLowerCase();
        const filtered = allJds.filter(jd =>
            jd.jobTitle.toLowerCase().includes(term) ||
            jd.location.toLowerCase().includes(term)
        );
        renderJds(filtered);
    });

    function checkAutoApply(jds) {
        const urlParams = new URLSearchParams(window.location.search);
        const openApplyId = urlParams.get('openApply');
        if (openApplyId) {
            const jd = jds.find(item => item.id == openApplyId);
            if (jd) setTimeout(() => openApplyModal(jd.id, jd.jobTitle), 300);
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    }

    function renderJds(jds) {
        if (jds.length === 0) {
            jdListEl.innerHTML = `<div class="jobs-no-results-empty">No available positions found.</div>`;
            return;
        }

        const isHR = localStorage.getItem('role') === 'HR';
        jdListEl.innerHTML = jds.map((jd, idx) => {
            const initials = jd.jobTitle.substring(0, 1).toUpperCase();
            const colors = ['#0f172a', '#334155', '#475569', '#1e293b'];
            const bgColor = colors[idx % colors.length];
            const isFullTime = jd.jobType.includes('FULL');
            const badgeClass = isFullTime ? 'badge-fulltime' : 'badge-remote';
            const badgeText = isFullTime ? 'Full-time' : 'Remote';
            return `
                <div class="ref-card">
                    <div class="ref-card-header">
                        <div class="company-initial" style="--company-bg-color: ${bgColor}">${initials}</div>
                        <div class="${badgeClass}">${badgeText}</div>
                    </div>
                    <div class="ref-card-title">${jd.jobTitle}</div>
                    <div class="ref-card-desc">${jd.jobDescription || 'Apply to explore this role.'}</div>
                    <div class="ref-card-footer">
                        <div class="ref-location">${jd.location}</div>
                        <div class="jobs-action-row">
                            ${isHR ? `<button class="btn-apply-light jobs-delete-btn" onclick="deleteJd(${jd.id})">Delete</button>` : ''}
                            <button class="btn-apply-light" onclick="openApplyModal(${jd.id}, '${jd.jobTitle.replace(/'/g, "\\'")}')">Apply Now</button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    }
});

const modal = document.getElementById('applyModal');

async function canCandidateApply() {
    try {
        const profileRes = await api.getMyProfile();
        const profile = profileRes.data;
        return !profile || profile.currentStage === 'REJECTED';
    } catch (e) {
        return true;
    }
}

async function openApplyModal(jdId, jobTitle) {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    if (!token) {
        window.location.href = `../auth/login.html?redirectJobId=${jdId}`;
        return;
    }
    if (role === 'HR' || role === 'PANEL') {
        alert('Only candidates can apply for jobs.');
        return;
    }

    const eligible = await canCandidateApply();
    if (!eligible) {
        alert('You already have an active application. You can apply again only after HR marks it Rejected.');
        return;
    }

    document.getElementById('modalJobTitle').innerText = jobTitle;
    document.getElementById('applyJdId').value = jdId;
    document.getElementById('applyForm').reset();
    document.getElementById('applyErrorMsg').style.display = 'none';
    document.getElementById('applySuccessMsg').style.display = 'none';
    modal.classList.add('active');
}

function closeApplyModal() {
    modal.classList.remove('active');
}

function submitApplication() {
    const jdId = document.getElementById('applyJdId').value;
    const errorMsg = document.getElementById('applyErrorMsg');
    const successMsg = document.getElementById('applySuccessMsg');
    const form = document.getElementById('applyForm');

    if (!form.checkValidity()) {
        errorMsg.innerText = 'Please fill out all required fields.';
        errorMsg.style.display = 'block';
        return;
    }

    errorMsg.style.display = 'none';
    const submitBtn = document.querySelector('.modal-footer .primary-btn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Submitting...';

    const payload = {
        jobDescriptionId: parseInt(jdId),
        fullName: document.getElementById('applyName').value,
        email: document.getElementById('applyEmail').value,
        mobileCode: document.getElementById('applyMobileCode').value,
        mobileNumber: document.getElementById('applyMobile').value,
        dateOfBirth: document.getElementById('applyDob').value || null,
        currentOrganization: document.getElementById('applyOrg').value,
        preferredLocation: document.getElementById('applyLocation').value,
        totalExperience: parseFloat(document.getElementById('applyTotalExp').value),
        relevantExperience: parseFloat(document.getElementById('applyRelExp').value),
        currentCtc: parseFloat(document.getElementById('applyCurrentCtc').value),
        expectedCtc: parseFloat(document.getElementById('applyExpectedCtc').value),
        noticePeriod: parseInt(document.getElementById('applyNotice').value),
        source: document.getElementById('applySource').value
    };

    api.registerCandidate(payload)
        .then(data => {
            const candidateId = data.data.id;
            const resumeFile = document.getElementById('applyResume').files[0];
            return api.uploadResume(candidateId, resumeFile);
        })
        .then(() => {
            successMsg.innerHTML = '<strong>Application successful!</strong><br>You will be redirected to your dashboard.';
            successMsg.style.display = 'block';
            setTimeout(() => {
                closeApplyModal();
                window.location.href = '../candidate/dashboard.html';
            }, 1500);
        })
        .catch(err => {
            errorMsg.innerText = err.message || 'An error occurred.';
            errorMsg.style.display = 'block';
        })
        .finally(() => {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Submit Application';
        });
}

function handleAuth() {
    if (localStorage.getItem('token')) {
        localStorage.clear();
        window.location.reload();
    } else {
        window.location.href = '../auth/login.html';
    }
}

window.openApplyModal = openApplyModal;
window.closeApplyModal = closeApplyModal;
window.submitApplication = submitApplication;
window.handleAuth = handleAuth;
