// ============================================================
// signup.js — candidate account creation
// ============================================================

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('signupForm');
    const errorDiv = document.getElementById('signupError');
    const submitBtn = form ? form.querySelector('button[type="submit"]') : null;

    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (errorDiv) errorDiv.style.display = 'none';

        const fullName = document.getElementById('fullName')?.value?.trim();
        const email = document.getElementById('email')?.value?.trim();
        const password = document.getElementById('password')?.value || '';
        const confirmPassword = document.getElementById('confirmPassword')?.value || '';

        if (!fullName || !email || !password || !confirmPassword) {
            if (errorDiv) {
                errorDiv.textContent = 'Please fill all required fields.';
                errorDiv.style.display = 'block';
            }
            return;
        }

        if (password.length < 6) {
            if (errorDiv) {
                errorDiv.textContent = 'Password must be at least 6 characters.';
                errorDiv.style.display = 'block';
            }
            return;
        }

        if (password !== confirmPassword) {
            if (errorDiv) {
                errorDiv.textContent = 'Password and Confirm Password do not match.';
                errorDiv.style.display = 'block';
            }
            return;
        }

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Creating Account...';
        }

        try {
            const result = await api.signup(fullName, email, password);
            if (!result.success) throw new Error(result.message || 'Signup failed');

            if (result.data && result.data.token) {
                auth.save(result.data);
                auth.redirectByRole();
                return;
            }

            window.location.href = 'login.html';
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
