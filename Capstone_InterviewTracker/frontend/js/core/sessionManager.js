// Session helpers used by role guard / dashboard bootstrap.
(function () {
    function getBasePath() {
        const pathname = window.location.pathname;
        const depth = (pathname.match(/\//g) || []).length;
        let base = '';
        for (let i = 2; i < depth; i++) {
            base += '../';
        }
        return base;
    }

    const SessionManager = {
        getToken: function () { return localStorage.getItem('token'); },
        getRole: function () { return localStorage.getItem('role'); },
        getFullName: function () { return localStorage.getItem('fullName') || 'User'; },
        getEmail: function () { return localStorage.getItem('userEmail') || ''; },
        clear: function () { localStorage.clear(); },
        getBasePath: getBasePath
    };

    window.SessionManager = SessionManager;
})();
