// ============================================================
// verify-email.js — handles email verification redirect
// ============================================================

document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    
    if (token) {
        // Redirect to activation page with token
        window.location.replace(`activate.html?token=${encodeURIComponent(token)}`);
    } else {
        // No token provided - redirect to activate anyway
        window.location.replace('activate.html');
    }
});
