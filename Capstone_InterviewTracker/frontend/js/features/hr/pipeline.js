document.addEventListener('DOMContentLoaded', () => {
    const session = window.HireTrackDashboard.bootstrapDashboard('HR', {
        roleLabel: 'HR',
        defaultTitle: 'HR Manager',
        avatarFallback: 'HR'
    });
    if (!session) {
        return;
    }
});
