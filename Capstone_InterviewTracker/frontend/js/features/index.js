document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    const userEmail = localStorage.getItem('userEmail');

    const authSection = document.getElementById('authSection');

    if (token) {
        let dashboardLink = "pages/candidate/dashboard.html";
        if (role === 'HR') dashboardLink = "pages/hr/overview.html";
        else if (role === 'PANEL') dashboardLink = "pages/panel/overview.html";

        authSection.innerHTML = `
            <a href="${dashboardLink}" class="login-btn" style="text-decoration: none;">Dashboard</a>
            <button class="signup-btn" onclick="handleAuth()">Sign Out</button>
        `;
    }

    // Load and render jobs
    loadAndRenderJobs();
});

let allJobs = [];

function loadAndRenderJobs() {
    const jobsGrid = document.getElementById('jobsGrid');
    const searchInput = document.getElementById('jobsSearchInput');

    // Load jobs from API
    api.getAllPublicJds()
        .then(result => {
            allJobs = result.data || [];
            renderJobs(allJobs);
        })
        .catch(err => {
            jobsGrid.innerHTML = `<div style="grid-column:1/-1; text-align:center; color:#ef4444;">Error loading positions. Please try again later.</div>`;
        });

    // Search functionality
    searchInput.addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allJobs.filter(jd =>
            jd.jobTitle.toLowerCase().includes(term) ||
            jd.location.toLowerCase().includes(term)
        );
        renderJobs(filtered);
    });
}

function renderJobs(jobs) {
    const jobsGrid = document.getElementById('jobsGrid');

    if (jobs.length === 0) {
        jobsGrid.innerHTML = `<div style="grid-column:1/-1; text-align:center; color:#64748b; padding:40px;">No positions available.</div>`;
        return;
    }

    jobsGrid.innerHTML = jobs.map((jd, idx) => {
        const initials = jd.jobTitle.substring(0, 1).toUpperCase();
        const colors = ['#0f172a', '#334155', '#475569', '#1e293b'];
        const bgColor = colors[idx % colors.length];
        const isFullTime = jd.jobType.includes('FULL');
        const badgeClass = isFullTime ? 'badge-fulltime' : 'badge-remote';
        const badgeText = isFullTime ? 'Full-time' : 'Remote';
        return `
            <div class="ref-card">
                <div class="ref-card-header">
                    <div class="company-initial" style="background: ${bgColor}">${initials}</div>
                    <div class="${badgeClass}">${badgeText}</div>
                </div>
                <div class="ref-card-title">${jd.jobTitle}</div>
                <div class="ref-card-desc">${jd.jobDescription || 'Apply to explore this role.'}</div>
                <div class="ref-card-footer">
                    <div class="ref-location">${jd.location}</div>
                    <button class="btn-apply-light" onclick="openApplyModal(${jd.id}, '${jd.jobTitle.replace(/'/g, "\\'")}')">Apply Now</button>
                </div>
            </div>
        `;
    }).join('');
}

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
        window.location.href = `pages/auth/login.html?redirectJobId=${jdId}`;
        return;
    }

    if (role === 'HR' || role === 'PANEL') {
        alert("Only candidates can apply for jobs.");
        return;
    }

    const eligible = await canCandidateApply();
    if (!eligible) {
        alert('You already have an active application. You can apply again only after HR marks it Rejected.');
        return;
    }

    document.getElementById('modalJobTitle').innerText = jobTitle;
    document.getElementById('applyJdId').value = jdId;

    // Clear form
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

    const email = document.getElementById('applyEmail').value;
    const mobile = document.getElementById('applyMobile').value;
    const dob = document.getElementById('applyDob').value;

    if (dob) {
        const birthDate = new Date(dob);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        if (age < 18) {
            errorMsg.innerText = "Candidate must be at least 18 years old.";
            errorMsg.style.display = "block";
            return;
        }
        if (birthDate.getFullYear() < 1950) {
            errorMsg.innerText = "Please provide a valid Date of Birth (Year > 1950).";
            errorMsg.style.display = "block";
            return;
        }
    }

    // Strict Email Validation (requires @ and .something)
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(email)) {
        errorMsg.innerText = "Please provide a valid email (e.g. name@example.com).";
        errorMsg.style.display = "block";
        return;
    }

    // Mobile Number Validation (Exactly 10 digits)
    if (mobile.length !== 10) {
        errorMsg.innerText = "Mobile number must be exactly 10 digits.";
        errorMsg.style.display = "block";
        return;
    }

    errorMsg.style.display = 'none';
    const submitBtn = document.querySelector('.modal-footer .primary-btn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Submitting...';

    const totalExp = parseFloat(document.getElementById('applyTotalExp').value);
    const relExp = parseFloat(document.getElementById('applyRelExp').value);

    if (relExp > totalExp) {
        errorMsg.innerText = "Relevant experience cannot be greater than total experience.";
        errorMsg.style.display = "block";
        return;
    }

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
        source: document.getElementById('applySource').value,
        gender: document.getElementById('applyGender').value
    };

    api.registerCandidate(payload)
        .then(data => {
            const candidateId = data.data.id;
            const resumeFile = document.getElementById('applyResume').files[0];
            return api.uploadResume(candidateId, resumeFile);
        })
        .then(data => {
            successMsg.innerHTML = "<strong>Application successful!</strong><br>You will be redirected to your dashboard.";
            successMsg.style.display = 'block';
            setTimeout(() => {
                closeApplyModal();
                window.location.href = "pages/candidate/dashboard.html";
            }, 2000);
        })
        .catch(err => {
            errorMsg.innerText = err.message || "An error occurred.";
            errorMsg.style.display = "block";
        })
        .finally(() => {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Submit Application';
        });
}

function handleAuth() {
    if (localStorage.getItem('token')) {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('userEmail');
        window.location.reload();
    } else {
        window.location.href = 'pages/auth/login.html';
    }
}