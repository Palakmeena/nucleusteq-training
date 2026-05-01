auth.requireRole('HR');
document.getElementById('sidebar').innerHTML = buildSidebar('HR', 'jobs');

let skills = [];
let isEditMode = false;
let editingJobId = null;
let allHrJobs = [];
const modal = document.getElementById('jdModal');

function shortText(text, max = 180) {
    const value = (text || '').trim();
    if (!value) return 'No description available.';
    return value.length > max ? value.slice(0, max) + '...' : value;
}

function openJobDetails(id) {
    const jd = allHrJobs.find(item => item.id === id);
    if (!jd) return;
    showJdModal({ title: jd.jobTitle, details: jd.jobDescription || 'No details provided.' });
}

function openModal() {
    isEditMode = false;
    editingJobId = null;
    document.getElementById('modalTitle').textContent = 'Create Job Description';
    document.querySelector('#createJdForm button[type="submit"]').textContent = 'Publish Position';
    document.getElementById('createJdForm').reset();
    skills = [];
    renderSkills();
    modal.classList.add('active');
}

function closeModal() {
    modal.classList.remove('active');
    document.getElementById('createJdForm').reset();
    isEditMode = false;
    editingJobId = null;
    skills = [];
    renderSkills();
}

document.getElementById('skillInput').addEventListener('keydown', e => {
    if (e.key === 'Enter') {
        e.preventDefault();
        const val = e.target.value.trim();
        if (val && !skills.includes(val)) {
            skills.push(val);
            renderSkills();
        }
        e.target.value = '';
    }
});

function renderSkills() {
    document.getElementById('skillsList').innerHTML = skills.map((s, i) =>
        `<span class="skill-chip">${s}<button type="button" onclick="removeSkill(${i})">×</button></span>`
    ).join('');
}

function removeSkill(i) {
    skills.splice(i, 1);
    renderSkills();
}

document.getElementById('createJdForm').addEventListener('submit', async e => {
    e.preventDefault();

    // Auto-add if user typed something but didn't press enter
    const skillInput = document.getElementById('skillInput');
    const remainingSkill = skillInput.value.trim();
    if (remainingSkill && !skills.includes(remainingSkill)) {
        skills.push(remainingSkill);
    }

    if (skills.length === 0) {
        showToast('Please add at least one skill (press Enter)', 'error');
        return;
    }

    const minE = parseFloat(document.getElementById('minExp').value);
    const maxE = parseFloat(document.getElementById('maxExp').value);
    const minS = parseFloat(document.getElementById('minSalary').value);
    const maxS = parseFloat(document.getElementById('maxSalary').value);

    if (minE < 0 || maxE < 0 || minS < 0 || maxS < 0) {
        showToast('Experience and Salary values cannot be negative', 'error');
        return;
    }
    if (minE > maxE) {
        showToast('Minimum experience cannot be greater than maximum', 'error');
        return;
    }
    if (minS > maxS) {
        showToast('Minimum salary cannot be greater than maximum', 'error');
        return;
    }

    const body = {
        jobTitle: document.getElementById('jobTitle').value,
        location: document.getElementById('location').value,
        jobType: document.getElementById('jobType').value,
        minExperience: minE,
        maxExperience: maxE,
        minSalary: minS,
        maxSalary: maxS,
        jobDescription: document.getElementById('description').value,
        skills: skills
    };

    try {
        if (isEditMode) {
            const res = await api.updateJd(editingJobId, body);
            if (!res.success) throw new Error(res.message);
            showToast('Job updated successfully!', 'success');
        } else {
            const res = await api.createJd(body);
            if (!res.success) throw new Error(res.message);
            showToast('Job posted successfully!', 'success');
        }
        closeModal();
        loadJobs();
    } catch (e) {
        showToast(e.message, 'error');
    }
});

async function loadJobs() {
    try {
        const res = await api.getAllJdsForHr();
        const allJds = res.data || [];
        allHrJobs = allJds;
        const jds = allJds.filter(jd => jd.active);
        const container = document.getElementById('hrJobsList');
        if (!jds.length) {
            container.innerHTML = '<p style="color:#64748b;text-align:center;padding:40px;">No active job descriptions right now.</p>';
            return;
        }
        const typeLabel = { FULL_TIME: 'Full Time', CONTRACT: 'Contract', REMOTE: 'Remote' };
        container.innerHTML = jds.map(jd => `
            <div class="job-row-card">
                <div style="flex:1;min-width:0;">
                    <h3 class="job-title">${jd.jobTitle}</h3>
                    <div class="job-meta"> ${jd.location} &nbsp;|&nbsp;  ${typeLabel[jd.jobType] || jd.jobType} &nbsp;|&nbsp;  ${jd.minExperience}-${jd.maxExperience} yrs &nbsp;|&nbsp;  ${jd.minSalary}-${jd.maxSalary} LPA</div>
                    <div class="job-desc">${shortText(jd.jobDescription)}</div>
                    <div class="job-tags">
                        ${(jd.skills || []).map(s => `<span class="tag">${s}</span>`).join('')}
                    </div>
                </div>
                <div style="display:flex;flex-direction:column;align-items:flex-end;gap:8px;min-width:210px;">
                    <span class="jd-badge" style="background:#dcfce7;color:#16a34a;">Active</span>
                    <div style="display:flex;gap:8px;flex-wrap:wrap;justify-content:flex-end;">
                        <button class="secondary-btn" style="padding:6px 12px;font-size:13px;" onclick="openJobDetails(${jd.id})">View Job</button>
                        <button class="secondary-btn" style="padding:6px 12px;font-size:13px;" onclick="editJd(${jd.id})">Edit</button>
                        <button class="secondary-btn" style="padding:6px 12px;font-size:13px;color:#dc2626;" onclick="deleteJd(${jd.id})">Delete</button>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (e) {
        showToast('Failed to load jobs: ' + e.message, 'error');
    }
}

async function editJd(id) {
    try {
        const res = await api.getJdById(id);
        const jd = res.data;

        isEditMode = true;
        editingJobId = id;

        document.getElementById('modalTitle').textContent = 'Edit Job Description';
        document.querySelector('#createJdForm button[type="submit"]').textContent = 'Update Position';

        document.getElementById('jobTitle').value = jd.jobTitle;
        document.getElementById('location').value = jd.location;
        document.getElementById('jobType').value = jd.jobType;
        document.getElementById('minExp').value = jd.minExperience;
        document.getElementById('maxExp').value = jd.maxExperience;
        document.getElementById('minSalary').value = jd.minSalary;
        document.getElementById('maxSalary').value = jd.maxSalary;
        document.getElementById('description').value = jd.jobDescription;

        skills = jd.skills ? [...jd.skills] : [];
        renderSkills();

        modal.classList.add('active');
    } catch (e) {
        showToast('Failed to load job details: ' + e.message, 'error');
    }
}

async function deleteJd(id) {
    if (!confirm('Are you sure you want to completely delete this job description?')) return;
    try {
        await api.deleteJd(id);
        showToast('Job deleted successfully', 'success');
        loadJobs();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

window.openModal = openModal;
window.closeModal = closeModal;
window.openJobDetails = openJobDetails;
window.editJd = editJd;
window.deleteJd = deleteJd;
window.removeSkill = removeSkill;

loadJobs();
