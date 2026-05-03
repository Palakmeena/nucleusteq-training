// Candidate dashboard feature script
// Dependencies: api, auth, utils (formatDate, svg icons, stageBadge), buildSidebar

(function () {
    try {
        auth.requireRole('CANDIDATE');
    } catch (e) {
        // auth may throw if not initialized; let it surface in console
    }

    document.getElementById('sidebar').innerHTML = buildSidebar('CANDIDATE', 'dashboard');
    document.getElementById('welcomeText').textContent = 'Welcome back, ' + (auth.getFullName ? auth.getFullName() : '') + '!';

    const stageOrder = ['PROFILING', 'SCREENING', 'L1_TECHNICAL', 'L2_TECHNICAL', 'HR_ROUND'];
    const stageLabels = { PROFILING: 'Profiling', SCREENING: 'Screening', L1_TECHNICAL: 'L1 Technical', L2_TECHNICAL: 'L2 Technical', HR_ROUND: 'HR Round', SELECTED: 'Selected', REJECTED: 'Rejected' };
    const stageNotes = {
        PROFILING: 'Your profile has been submitted. HR will review it shortly.',
        SCREENING: 'Your profile is being reviewed by HR.',
        L1_TECHNICAL: 'You have cleared screening! Prepare for your L1 Technical interview.',
        L2_TECHNICAL: 'Great job! You have cleared L1. Your L2 interview is next.',
        HR_ROUND: 'Excellent! You are in the final HR round.',
        SELECTED: ' Congratulations! You have been selected!',
        REJECTED: 'Thank you for your time. We wish you the best in your future endeavors.'
    };

    function setNoApplicationState() {
        document.getElementById('jobTitle').textContent = "You haven't applied to any job yet";
        document.getElementById('appliedDate').textContent = 'Go to Jobs tab from sidebar to apply.';
        document.getElementById('currentStageBadge').innerHTML = '<span class="jd-badge badge-gray dashboard-not-applied-badge">Not Applied</span>';
        document.getElementById('stageNote').textContent = 'Your progress will appear here once you submit an application.';
        document.getElementById('stageStepper').innerHTML = '';
        document.getElementById('stageLabelsRow').style.display = 'none';
    }

    async function loadDashboard() {
        try {
            const profileRes = await api.getMyProfile();
            const profile = profileRes.data;

            document.getElementById('jobTitle').textContent = profile.jobTitle || 'N/A';
            document.getElementById('appliedDate').textContent = 'Applied: ' + formatDate(profile.createdAt);

            if (profile.currentStage) {
                document.getElementById('currentStageBadge').innerHTML = stageBadge(profile.currentStage);
                document.getElementById('stageNote').textContent = stageNotes[profile.currentStage] || '';
            }

            const svgIcons = {
                PROFILING: svgUsers,
                SCREENING: svgEye,
                L1_TECHNICAL: svgCalendar,
                L2_TECHNICAL: svgCalendar,
                HR_ROUND: svgUserCheck
            };

            // Build stepper
            const stepper = document.getElementById('stageStepper');
            const currentIdx = stageOrder.indexOf(profile.currentStage);
            stepper.innerHTML = stageOrder.map((stage, i) => {
                let cls = '';
                if (i < currentIdx) cls = 'done';
                else if (i === currentIdx) cls = 'active';
                
                const icon = i < currentIdx ? svgCheck : (svgIcons[stage] || (i + 1));
                return `<div class="stage-step"><div class="stage-dot ${cls}">${icon}</div></div>`;
            }).join('');
            document.getElementById('stageLabelsRow').style.display = 'flex';

            // Load interviews
            try {
                const iRes = await api.getMyInterviews();
                // Only show if NOT selected and there is a pending interview
                const interviews = (iRes.data || []).filter(i => !i.completed);
                
                if (interviews.length && profile.currentStage !== 'SELECTED' && profile.currentStage !== 'REJECTED') {
                    const next = interviews[0];
                    document.getElementById('interviewSection').style.display = 'block';
                    document.getElementById('noInterviewSection').style.display = 'none';
                    document.getElementById('iDate').textContent = formatDate(next.interviewDate);
                    document.getElementById('iTime').textContent = next.interviewTime || '—';
                    document.getElementById('iStage').textContent = stageLabels[next.interviewStage] || next.interviewStage;

                    // Add Join Meeting button if link exists
                    const existingBtn = document.getElementById('joinBtnContainer');
                    if (existingBtn) existingBtn.remove();

                    if (next.meetingLink) {
                        const btnHtml = `
                            <div class="dashboard-join-btn-container">
                                <a href="${next.meetingLink}" target="_blank" class="dashboard-join-btn">
                                    Join Online Interview
                                </a>
                            </div>
                        `;
                        document.getElementById('interviewSection').insertAdjacentHTML('beforeend', btnHtml);
                    }
                }
            } catch (e) { /* interviews not yet scheduled */ }

        } catch (e) {
            setNoApplicationState();
        }

    }

    // Kick off
    loadDashboard();
})();
