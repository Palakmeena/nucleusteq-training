import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import authApi from '../../api/authApi';
import { useAuth } from '../../context/AuthContext';
import { getDashboardPath } from '../../constants/roles';
import { showError } from '../../utils/errorHandler';
import './AuthPages.css';

const EyeIcon = ({ open }) => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    {open ? (
      <>
        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
        <circle cx="12" cy="12" r="3" />
      </>
    ) : (
      <>
        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
        <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
        <line x1="1" y1="1" x2="23" y2="23" />
      </>
    )}
  </svg>
);

const ShieldIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
  </svg>
);

const ClockIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="12" r="10" />
    <polyline points="12 6 12 12 16 14" />
  </svg>
);

const StarIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
  </svg>
);

const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: { email: '', password: '' } });

  const onSubmit = async (data) => {
    try {
      const response = await authApi.login(data);
      const { access_token, role, full_name } = response.data;
      login(access_token, { fullName: full_name });
      toast.success('Welcome back!');
      navigate(getDashboardPath(role));
    } catch (error) {
      showError(error, 'Login failed. Please check your credentials.');
    }
  };

  return (
    <div className="auth-page">
      {/* Left Brand Panel */}
      <div className="auth-brand-panel">
        <div className="auth-brand-content">
          <div className="auth-logo-block">
            <div className="auth-logo-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
              </svg>
            </div>
            <span className="auth-brand-name">MedPulse</span>
          </div>

          <div className="auth-brand-text">
            <h2>Healthcare at your fingertips</h2>
            <p>Connect with verified doctors, book appointments, and manage your health — all in one place.</p>
          </div>

          <div className="auth-feature-list">
            <div className="auth-feature-item">
              <div className="auth-feature-icon">
                <ShieldIcon />
              </div>
              <div>
                <strong>Verified Doctors</strong>
                <span>Every specialist is background-checked and credentialed</span>
              </div>
            </div>
            <div className="auth-feature-item">
              <div className="auth-feature-icon">
                <ClockIcon />
              </div>
              <div>
                <strong>24/7 Access</strong>
                <span>Book in-person or virtual consultations any time</span>
              </div>
            </div>
            <div className="auth-feature-item">
              <div className="auth-feature-icon">
                <StarIcon />
              </div>
              <div>
                <strong>Trusted by Millions</strong>
                <span>Over 2 million patients trust MedPulse for their care</span>
              </div>
            </div>
          </div>

          <div className="auth-brand-footer">
            <div className="auth-avatars">
              <div className="auth-avatar" style={{ background: '#60a5fa' }}>S</div>
              <div className="auth-avatar" style={{ background: '#34d399' }}>M</div>
              <div className="auth-avatar" style={{ background: '#f472b6' }}>E</div>
            </div>
            <span>Joined by 10,000+ specialists this month</span>
          </div>
        </div>
      </div>

      {/* Right Form Panel */}
      <div className="auth-form-panel">
        <div className="auth-form-wrapper">
          <div className="auth-form-header">
            <h1>Welcome back</h1>
            <p>Sign in to your MedPulse account</p>
          </div>

          <form className="auth-form-body" onSubmit={handleSubmit(onSubmit)}>
            {/* Email */}
            <div className="auth-field">
              <label htmlFor="login-email">Email address</label>
              <div className="auth-input-group">
                <span className="auth-input-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                    <polyline points="22,6 12,13 2,6" />
                  </svg>
                </span>
                <input
                  id="login-email"
                  type="email"
                  placeholder="alex.river@example.com"
                  className={`auth-input ${errors.email ? 'auth-input--error' : ''}`}
                  {...register('email', { required: 'Email is required' })}
                />
              </div>
              {errors.email && <span className="auth-field-error">{errors.email.message}</span>}
            </div>

            {/* Password */}
            <div className="auth-field">
              <label htmlFor="login-password">Password</label>
              <div className="auth-input-group">
                <span className="auth-input-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                </span>
                <input
                  id="login-password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="••••••••"
                  className={`auth-input ${errors.password ? 'auth-input--error' : ''}`}
                  {...register('password', { required: 'Password is required' })}
                />
                <button
                  type="button"
                  className="auth-input-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  <EyeIcon open={showPassword} />
                </button>
              </div>
              {errors.password && <span className="auth-field-error">{errors.password.message}</span>}
            </div>

            <button
              type="submit"
              className="auth-submit-btn"
              disabled={isSubmitting}
              id="login-submit-btn"
            >
              {isSubmitting ? (
                <span className="auth-btn-loading">
                  <span className="auth-spinner" />
                  Signing in...
                </span>
              ) : (
                'Sign In'
              )}
            </button>
          </form>

          <div className="auth-divider"><span>New to MedPulse?</span></div>

          <p className="auth-form-footer">
            Don&apos;t have an account?{' '}
            <Link to="/register" className="auth-link" id="go-to-register-link">
              Create one for free
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
