// ============================================================
// login.js — handles login form submission
// ============================================================

document.addEventListener('DOMContentLoaded', () => {

    // If already logged in — redirect directly
    if (localStorage.getItem('token')) {
        const role = localStorage.getItem('role');
        const base = getBasePath();
        if (role === 'HR')        window.location.href = base + 'pages/hr/overview.html';
        else if (role === 'PANEL')     window.location.href = base + 'pages/panel/overview.html';
        else if (role === 'CANDIDATE') window.location.href = base + 'pages/candidate/dashboard.html';
    }

    // Setup signup link with redirect parameter
    const urlParams = new URLSearchParams(window.location.search);
    const redirectJobId = urlParams.get('redirectJobId');
    const signupLink = document.getElementById("signupLink");
    if (signupLink) {
        if (redirectJobId) {
            signupLink.href = `signup.html?redirectJobId=${redirectJobId}`;
        } else {
            signupLink.href = "signup.html";
        }
    }

    const form = document.getElementById('loginForm');
    const errorDiv = document.getElementById('loginError');
    const submitBtn = form ? form.querySelector('button[type="submit"]') : null;

    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email    = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        if (errorDiv) errorDiv.style.display = 'none';
        if (submitBtn) { submitBtn.disabled = true; submitBtn.textContent = 'Signing in...'; }

        try {
            const result = await api.login(email, password);

            if (!result.success) throw new Error(result.message || 'Login failed');

            // Save user data
            auth.save(result.data);

            // Check if there's a pending job application redirect
            const params = new URLSearchParams(window.location.search);
            const redirectJobId = params.get('redirectJobId');

            if (redirectJobId && result.data.role === 'CANDIDATE') {
                window.location.href = getBasePath() + `index.html?openApply=${redirectJobId}`;
            } else {
                auth.redirectByRole();
            }

        } catch (err) {
            if (errorDiv) {
                errorDiv.textContent = err.message;
                errorDiv.style.display = 'block';
            } else {
                alert('Login failed: ' + err.message);
            }
        } finally {
            if (submitBtn) { submitBtn.disabled = false; submitBtn.textContent = 'Access Account'; }
        }
    });
});
