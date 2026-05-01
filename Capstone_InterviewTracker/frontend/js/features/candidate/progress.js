auth.requireRole('CANDIDATE');
document.getElementById('sidebar').innerHTML = buildSidebar('CANDIDATE', 'profile');

async function loadProfile() {
    try {
        const res = await api.getMyProfile();
        const p = res.data;
        const initials = (p.fullName || 'User').split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
        document.getElementById('profileContent').innerHTML = `
            <div class="profile-shell">
                <div class="profile-hero">
                    <div class="hero-left">
                        <div class="avatar">${initials}</div>
                        <div>
                            <div class="hero-name">${p.fullName}</div>
                            <div class="hero-email">${p.email}</div>
                        </div>
                    </div>
                    <div class="status-chip">
                        Current Stage
                        ${stageBadge(p.currentStage)}
                    </div>
                </div>

                <div class="section">
                    <h3 class="section-title">Personal Information</h3>
                    <div class="info-grid">
                        <div><div class="info-item-label">Full Name</div><div class="info-item-value">${p.fullName}</div></div>
                        <div><div class="info-item-label">Email</div><div class="info-item-value">${p.email}</div></div>
                        <div><div class="info-item-label">Mobile</div><div class="info-item-value">${p.mobileCode} ${p.mobileNumber}</div></div>
                        <div><div class="info-item-label">Date of Birth</div><div class="info-item-value">${p.dateOfBirth ? formatDate(p.dateOfBirth) : '—'}</div></div>
                    </div>
                </div>

                <div class="section">
                    <h3 class="section-title">Professional Details</h3>
                    <div class="info-grid">
                        <div><div class="info-item-label">Current Organization</div><div class="info-item-value">${p.currentOrganization}</div></div>
                        <div><div class="info-item-label">Total Experience</div><div class="info-item-value">${p.totalExperience} years</div></div>
                        <div><div class="info-item-label">Relevant Experience</div><div class="info-item-value">${p.relevantExperience} years</div></div>
                        <div><div class="info-item-label">Current CTC</div><div class="info-item-value">${p.currentCtc} LPA</div></div>
                        <div><div class="info-item-label">Expected CTC</div><div class="info-item-value">${p.expectedCtc} LPA</div></div>
                        <div><div class="info-item-label">Notice Period</div><div class="info-item-value">${p.noticePeriod} days</div></div>
                        <div><div class="info-item-label">Preferred Location</div><div class="info-item-value">${p.preferredLocation}</div></div>
                        <div><div class="info-item-label">Source</div><div class="info-item-value">${p.source}</div></div>
                    </div>
                </div>

                <div class="section">
                    <h3 class="section-title">Application & Resume</h3>
                    <div class="info-grid">
                        <div><div class="info-item-label">Applied For</div><div class="info-item-value">${p.jobTitle}</div></div>
                        <div><div class="info-item-label">Applied On</div><div class="info-item-value">${formatDate(p.createdAt)}</div></div>
                        <div><div class="info-item-label">Resume</div><div class="info-item-value">${p.resumePath ? '<span class="resume-pill ok"> Uploaded</span>' : '<span class="resume-pill miss">Resume Missing</span>'}</div></div>
                    </div>
                </div>
            </div>
        `;
    } catch (e) {
        showToast('Failed to load profile: ' + e.message, 'error');
    }
}

loadProfile();
