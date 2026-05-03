const skills = [];

function addSkill() {
    const input = document.getElementById('skillInput');
    const val = input.value.trim();
    if (val && !skills.includes(val)) {
        skills.push(val);
        renderSkills();
    }
    input.value = '';
    input.focus();
}

document.getElementById('skillInput').addEventListener('keydown', e => {
    if (e.key === 'Enter') {
        e.preventDefault();
        addSkill();
    }
});

function removeSkill(i) {
    skills.splice(i, 1);
    renderSkills();
}

function renderSkills() {
    document.getElementById('skillsList').innerHTML = skills.map((s, i) =>
        `<span class="skill-chip">${s}<button onclick="removeSkill(${i})">×</button></span>`
    ).join('');
}

document.getElementById('createJdForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login first.');
        window.location.href = '../auth/login.html';
        return;
    }
    if (skills.length === 0) {
        alert('Please add at least one skill.');
        return;
    }
    const body = {
        jobTitle: document.getElementById('jobTitle').value,
        jobDescription: document.getElementById('description').value,
        skills: skills,
        minExperience: parseInt(document.getElementById('minExp').value),
        maxExperience: parseInt(document.getElementById('maxExp').value),
        minSalary: parseFloat(document.getElementById('minSalary').value),
        maxSalary: parseFloat(document.getElementById('maxSalary').value),
        location: document.getElementById('location').value,
        jobType: document.getElementById('jobType').value
    };
    console.log('Sending JD:', JSON.stringify(body));
    try {
        const result = await window.api.createJd(body);
        console.log('Response:', result);
        if (result.success) {
            alert('Job posted successfully!');
            window.location.href = '../../index.html';
        } else {
            alert('Failed: ' + result.message);
        }
    } catch (err) {
        alert('Error: ' + err.message);
    }
});

window.addSkill = addSkill;
window.removeSkill = removeSkill;
