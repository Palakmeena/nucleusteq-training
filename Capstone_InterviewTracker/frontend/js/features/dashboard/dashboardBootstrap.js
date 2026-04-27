// Dashboard bootstrap compatibility layer.
(function () {
    function bootstrapDashboard(expectedRole, options) {
        if (!window.RoleGuard.requireRole(expectedRole)) {
            return null;
        }

        return {
            token: window.SessionManager.getToken(),
            role: window.SessionManager.getRole(),
            fullName: window.SessionManager.getFullName(),
            email: window.SessionManager.getEmail(),
            options: options || {}
        };
    }

    window.HireTrackDashboard = {
        bootstrapDashboard: bootstrapDashboard
    };
})();
