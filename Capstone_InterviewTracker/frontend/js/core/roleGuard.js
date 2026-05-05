// Role guard used by dashboard bootstrap.
// IIFE pattern used to maintain scope isolation:
// - redirectByRole() remains private and inaccessible from other files
// - Only RoleGuard object is exposed to window global scope
(function () {
    function redirectByRole() {
        const role = window.SessionManager.getRole();
        const base = window.SessionManager.getBasePath();
        if (role === 'HR') window.location.href = base + 'pages/hr/overview.html';
        else if (role === 'PANEL') window.location.href = base + 'pages/panel/overview.html';
        else if (role === 'CANDIDATE') window.location.href = base + 'pages/candidate/dashboard.html';
        else window.location.href = base + 'index.html';
    }

    const RoleGuard = {
        requireRole: function (expectedRole) {
            const token = window.SessionManager.getToken();
            if (!token) {
                window.location.href = window.SessionManager.getBasePath() + 'pages/auth/login.html';
                return false;
            }

            const role = window.SessionManager.getRole();
            if (role !== expectedRole) {
                redirectByRole();
                return false;
            }
            return true;
        }
    };

    window.RoleGuard = RoleGuard;
})();
