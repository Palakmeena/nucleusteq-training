// ============================================================
// activate.js — handles account activation with password setup
// ============================================================

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('activateForm');
    const errorBox = document.getElementById('activateError');
    const successBox = document.getElementById('activateSuccess');
    const btn = document.getElementById('activateBtn');
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');

    if (!token) {
        if (errorBox) {
            errorBox.textContent = 'Invalid activation link. Token is missing.';
            errorBox.style.display = 'block';
        }
        if (btn) btn.disabled = true;
        return;
    }

    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (errorBox) errorBox.style.display = 'none';
        if (successBox) successBox.style.display = 'none';

        const password = document.getElementById('password')?.value;
        const confirmPassword = document.getElementById('confirmPassword')?.value;

        if (!password || password.length < 6) {
            if (errorBox) {
                errorBox.textContent = 'Password must be at least 6 characters.';
                errorBox.style.display = 'block';
            }
            return;
        }

        if (password !== confirmPassword) {
            if (errorBox) {
                errorBox.textContent = 'Password and Confirm Password do not match.';
                errorBox.style.display = 'block';
            }
            return;
        }

        if (btn) {
            btn.disabled = true;
            btn.textContent = 'Activating...';
        }

        try {
            const res = await api.activate(token, password);
            if (!res.success) throw new Error(res.message || 'Activation failed');

            if (successBox) {
                successBox.textContent = 'Account activated successfully. Redirecting to login...';
                successBox.style.display = 'block';
            }

            setTimeout(() => {
                window.location.href = 'login.html';
            }, 1500);
        } catch (err) {
            if (errorBox) {
                errorBox.textContent = err.message || 'Activation failed';
                errorBox.style.display = 'block';
            }
        } finally {
            if (btn) {
                btn.disabled = false;
                btn.textContent = 'Activate Account';
            }
        }
    });
});
