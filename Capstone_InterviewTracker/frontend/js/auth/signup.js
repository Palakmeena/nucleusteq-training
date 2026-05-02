// ============================================================
// signup.js — candidate account creation
// ============================================================

document.addEventListener('DOMContentLoaded', () => {
    // Setup login link with redirect parameter
    const urlParams = new URLSearchParams(window.location.search);
    const redirectJobId = urlParams.get('redirectJobId');
    const loginLink = document.getElementById("loginLink");
    if (loginLink) {
        if (redirectJobId) {
            loginLink.href = `login.html?redirectJobId=${redirectJobId}`;
        } else {
            loginLink.href = "login.html";
        }
    }

    const form = document.getElementById('signupForm');
    const errorDiv = document.getElementById('signupError');
    const submitBtn = form ? form.querySelector('button[type="submit"]') : null;

    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (errorDiv) errorDiv.style.display = 'none';

        const fullName = document.getElementById('fullName')?.value?.trim();
        const email = document.getElementById('email')?.value?.trim();
        const mobileNumber = document.getElementById('mobileNumber')?.value?.trim();
        const dateOfBirth = document.getElementById('dateOfBirth')?.value || '';
        const gender = document.getElementById('gender')?.value || '';

        if (!fullName || !email || !mobileNumber || !dateOfBirth || !gender) {
            if (errorDiv) {
                errorDiv.textContent = 'Please fill all required fields.';
                errorDiv.style.display = 'block';
            }
            return;
        }

        if (mobileNumber.length !== 10 || isNaN(mobileNumber)) {
            if (errorDiv) {
                errorDiv.textContent = 'Please enter a valid 10-digit phone number.';
                errorDiv.style.display = 'block';
            }
            return;
        }

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Sending link...';
        }

        try {
            const result = await api.signup({
                fullName,
                email,
                mobileCode: '+91',
                mobileNumber,
                dateOfBirth,
                gender
            });
            if (!result.success) throw new Error(result.message || 'Signup failed');

            // Success state - Show verification message
            form.innerHTML = `
                <div class="auth-success-state">
                    <div class="auth-success-icon">✉️</div>
                    <h2 class="auth-success-title">Check your email</h2>
                    <p class="auth-success-copy">
                        We've sent an activation link to <strong>${email}</strong>.<br>
                        Open the link, set your password, and then sign in.
                    </p>
                    <a href="login.html" class="primary-btn auth-success-link">Go to Login</a>
                </div>
            `;
        } catch (err) {
            if (errorDiv) {
                errorDiv.textContent = err.message || 'Registration failed';
                errorDiv.style.display = 'block';
            }
        } finally {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Create Account';
            }
        }
    });
});
