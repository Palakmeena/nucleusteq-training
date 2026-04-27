// ============================================================
// authService.js — login state, token, user info, redirects
// ============================================================

// Helper to compute relative path to root based on current page location
function getBasePath() {
    const pathname = window.location.pathname;
    const depth = (pathname.match(/\//g) || []).length;
    let base = '';
    for (let i = 2; i < depth; i++) {
        base += '../';
    }
    return base;
}

const auth = {

    // Save user data after successful login
    save(userData) {
        localStorage.setItem('token', userData.token);
        localStorage.setItem('role', userData.role);
        localStorage.setItem('fullName', userData.fullName);
        localStorage.setItem('userEmail', userData.email);
    },

    getToken()    { return localStorage.getItem('token'); },
    getRole()     { return localStorage.getItem('role'); },
    getFullName() { return localStorage.getItem('fullName'); },
    getEmail()    { return localStorage.getItem('userEmail'); },
    isLoggedIn()  { return !!localStorage.getItem('token'); },

    logout() {
        localStorage.clear();
        window.location.href = getBasePath() + 'pages/auth/login.html';
    },

    // Redirect to correct dashboard based on role
    redirectByRole() {
        const role = this.getRole();
        const base = getBasePath();
        if (role === 'HR')        window.location.href = base + 'pages/hr/overview.html';
        else if (role === 'PANEL')     window.location.href = base + 'pages/panel/overview.html';
        else if (role === 'CANDIDATE') window.location.href = base + 'pages/candidate/dashboard.html';
        else window.location.href = base + 'index.html';
    },

    // Call at top of every protected page
    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = getBasePath() + 'pages/auth/login.html';
            return false;
        }
        return true;
    },

    // Call at top of role-specific pages
    requireRole(role) {
        if (!this.requireAuth()) return false;
        if (this.getRole() !== role) {
            this.redirectByRole();
            return false;
        }
        return true;
    }
};

window.auth = auth;
