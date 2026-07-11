import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import authApi from '../../api/authApi';
import { showError } from '../../utils/errorHandler';
import { SPECIALIZATIONS } from '../../constants/specializations';
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

const RegisterPage = () => {
  const [role, setRole] = useState('PATIENT');
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { isSubmitting, errors },
  } = useForm();

  const onSubmit = async (data) => {
    try {
      if (role === 'PATIENT') {
        await authApi.registerPatient({
          full_name: data.full_name,
          email: data.email,
          password: data.password,
          phone: data.phone,
          gender: data.gender,
          date_of_birth: data.date_of_birth,
        });
        toast.success('Account created! Please sign in.');
        navigate('/login');
      } else {
        await authApi.registerDoctor({
          full_name: data.full_name,
          email: data.email,
          password: data.password,
          phone: data.phone,
          qualification: data.qualification,
          experience: Number(data.experience),
          license_number: data.license_number,
          specialization: data.specialization,
          consultation_fee: Number(data.consultation_fee),
          clinic_address: data.clinic_address,
        });
        toast.info('Registration submitted. An admin will activate your account.');
        navigate('/login', {
          state: { message: 'Your doctor profile is pending admin approval before you can sign in.' },
        });
      }
    } catch (error) {
      showError(error, 'Registration failed');
    }
  };

  return (
    <div className="auth-page auth-page--register">
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
            <h2>Join thousands of healthcare professionals</h2>
            <p>Whether you're a patient looking for care or a doctor ready to serve, MedPulse has you covered.</p>
          </div>

          <div className="auth-register-pills">
            <div className={`auth-role-pill ${role === 'PATIENT' ? 'auth-role-pill--active' : ''}`}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              <div>
                <strong>As a Patient</strong>
                <span>Book appointments, manage your health records</span>
              </div>
            </div>
            <div className={`auth-role-pill ${role === 'DOCTOR' ? 'auth-role-pill--active' : ''}`}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
              </svg>
              <div>
                <strong>As a Doctor</strong>
                <span>Manage your schedule and patient appointments</span>
              </div>
            </div>
          </div>

          <div className="auth-brand-stats">
            <div className="auth-stat">
              <strong>2M+</strong>
              <span>Patients</span>
            </div>
            <div className="auth-stat-divider" />
            <div className="auth-stat">
              <strong>10K+</strong>
              <span>Doctors</span>
            </div>
            <div className="auth-stat-divider" />
            <div className="auth-stat">
              <strong>50+</strong>
              <span>Specialties</span>
            </div>
          </div>
        </div>
      </div>

      {/* Right Form Panel */}
      <div className="auth-form-panel">
        <div className="auth-form-wrapper auth-form-wrapper--wide">
          <div className="auth-form-header">
            <h1>Create your account</h1>
            <p>Join MedPulse and take control of your health</p>
          </div>

          {/* Role Toggle */}
          <div className="auth-role-toggle">
            <button
              type="button"
              className={`auth-role-btn ${role === 'PATIENT' ? 'auth-role-btn--active' : ''}`}
              onClick={() => setRole('PATIENT')}
              id="register-as-patient-btn"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              Patient
            </button>
            <button
              type="button"
              className={`auth-role-btn ${role === 'DOCTOR' ? 'auth-role-btn--active' : ''}`}
              onClick={() => setRole('DOCTOR')}
              id="register-as-doctor-btn"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
              </svg>
              Doctor
            </button>
          </div>

          {role === 'DOCTOR' && (
            <div className="auth-notice">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
              Doctor accounts require admin approval before you can sign in.
            </div>
          )}

          <form className="auth-form-body" onSubmit={handleSubmit(onSubmit)}>
            {/* Common Fields */}
            <div className="auth-form-grid">
              <div className="auth-field auth-field--full">
                <label htmlFor="reg-fullname">Full Name</label>
                <div className="auth-input-group">
                  <span className="auth-input-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                      <circle cx="12" cy="7" r="4" />
                    </svg>
                  </span>
                  <input
                    id="reg-fullname"
                    type="text"
                    placeholder="John Doe"
                    className={`auth-input ${errors.full_name ? 'auth-input--error' : ''}`}
                    {...register('full_name', { required: 'Full name is required' })}
                  />
                </div>
                {errors.full_name && <span className="auth-field-error">{errors.full_name.message}</span>}
              </div>

              <div className="auth-field">
                <label htmlFor="reg-email">Email Address</label>
                <div className="auth-input-group">
                  <span className="auth-input-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                      <polyline points="22,6 12,13 2,6" />
                    </svg>
                  </span>
                  <input
                    id="reg-email"
                    type="email"
                    placeholder="john@example.com"
                    className={`auth-input ${errors.email ? 'auth-input--error' : ''}`}
                    {...register('email', { required: 'Email is required' })}
                  />
                </div>
                {errors.email && <span className="auth-field-error">{errors.email.message}</span>}
              </div>

              <div className="auth-field">
                <label htmlFor="reg-phone">Phone Number</label>
                <div className="auth-input-group">
                  <span className="auth-input-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 12 19.79 19.79 0 0 1 1.61 3.18 2 2 0 0 1 3.59 1h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 8.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
                    </svg>
                  </span>
                  <input
                    id="reg-phone"
                    type="tel"
                    placeholder="+1 234 567 8900"
                    className={`auth-input ${errors.phone ? 'auth-input--error' : ''}`}
                    {...register('phone', { required: 'Phone is required' })}
                  />
                </div>
                {errors.phone && <span className="auth-field-error">{errors.phone.message}</span>}
              </div>

              <div className="auth-field auth-field--full">
                <label htmlFor="reg-password">Password</label>
                <div className="auth-input-group">
                  <span className="auth-input-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                      <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                    </svg>
                  </span>
                  <input
                    id="reg-password"
                    type={showPassword ? 'text' : 'password'}
                    placeholder="••••••••"
                    className={`auth-input ${errors.password ? 'auth-input--error' : ''}`}
                    {...register('password', { required: 'Password is required' })}
                  />
                  <button
                    type="button"
                    className="auth-input-toggle"
                    onClick={() => setShowPassword(!showPassword)}
                    aria-label="Toggle password visibility"
                  >
                    <EyeIcon open={showPassword} />
                  </button>
                </div>
                {errors.password && <span className="auth-field-error">{errors.password.message}</span>}
              </div>

              {/* Patient-specific fields */}
              {role === 'PATIENT' && (
                <>
                  <div className="auth-field">
                    <label htmlFor="reg-gender">Gender</label>
                    <div className="auth-input-group">
                      <span className="auth-input-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <circle cx="12" cy="8" r="4" />
                          <path d="M16 16H8a4 4 0 0 0-4 4" />
                        </svg>
                      </span>
                      <select
                        id="reg-gender"
                        className="auth-input auth-select"
                        {...register('gender', { required: 'Gender is required' })}
                      >
                        <option value="">Select gender</option>
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                      </select>
                    </div>
                  </div>

                  <div className="auth-field">
                    <label htmlFor="reg-dob">Date of Birth</label>
                    <div className="auth-input-group">
                      <span className="auth-input-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                          <line x1="16" y1="2" x2="16" y2="6" />
                          <line x1="8" y1="2" x2="8" y2="6" />
                          <line x1="3" y1="10" x2="21" y2="10" />
                        </svg>
                      </span>
                      <input
                        id="reg-dob"
                        type="date"
                        className="auth-input"
                        {...register('date_of_birth', { required: 'Date of birth is required' })}
                      />
                    </div>
                  </div>
                </>
              )}

              {/* Doctor-specific fields */}
              {role === 'DOCTOR' && (
                <>
                  <div className="auth-field">
                    <label htmlFor="reg-qualification">Qualification</label>
                    <div className="auth-input-group">
                      <span className="auth-input-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M22 10v6M2 10l10-5 10 5-10 5z" />
                          <path d="M6 12v5c3 3 9 3 12 0v-5" />
                        </svg>
                      </span>
                      <input
                        id="reg-qualification"
                        type="text"
                        placeholder="MBBS, MD, etc."
                        className="auth-input"
                        {...register('qualification', { required: 'Qualification is required' })}
                      />
                    </div>
                  </div>

                  <div className="auth-field">
                    <label htmlFor="reg-experience">Experience (years)</label>
                    <div className="auth-input-group">
                      <span className="auth-input-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <rect x="2" y="7" width="20" height="14" rx="2" ry="2" />
                          <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
                        </svg>
                      </span>
                      <input
                        id="reg-experience"
                        type="number"
                        placeholder="5"
                        min="0"
                        className="auth-input"
                        {...register('experience', { required: 'Experience is required' })}
                      />
                    </div>
                  </div>

                  <div className="auth-field">
                    <label htmlFor="reg-license">License Number</label>
                    <div className="auth-input-group">
                      <span className="auth-input-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <rect x="2" y="3" width="20" height="14" rx="2" ry="2" />
                          <line x1="8" y1="21" x2="16" y2="21" />
                          <line x1="12" y1="17" x2="12" y2="21" />
                        </svg>
                      </span>
                      <input
                        id="reg-license"
                        type="text"
                        placeholder="MED-12345"
                        className="auth-input"
                        {...register('license_number', { required: 'License number is required' })}
                      />
                    </div>
                  </div>

                  <div className="auth-field">
                    <label htmlFor="reg-specialization">Specialization</label>
                    <div className="auth-input-group">
                      <span className="auth-input-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
                        </svg>
                      </span>
                      <select
                        id="reg-specialization"
                        className="auth-input auth-select"
                        {...register('specialization', { required: 'Specialization is required' })}
                      >
                        <option value="">Select specialization</option>
                        {SPECIALIZATIONS.map((s) => (
                          <option key={s.value} value={s.value}>{s.label}</option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="auth-field">
                    <label htmlFor="reg-fee">Consultation Fee ($)</label>
                    <div className="auth-input-group">
                      <span className="auth-input-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <line x1="12" y1="1" x2="12" y2="23" />
                          <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
                        </svg>
                      </span>
                      <input
                        id="reg-fee"
                        type="number"
                        placeholder="100"
                        min="0"
                        className="auth-input"
                        {...register('consultation_fee', { required: 'Consultation fee is required' })}
                      />
                    </div>
                  </div>

                  <div className="auth-field auth-field--full">
                    <label htmlFor="reg-clinic">Clinic Address</label>
                    <div className="auth-input-group">
                      <span className="auth-input-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                          <circle cx="12" cy="10" r="3" />
                        </svg>
                      </span>
                      <input
                        id="reg-clinic"
                        type="text"
                        placeholder="123 Medical Center, City"
                        className="auth-input"
                        {...register('clinic_address', { required: 'Clinic address is required' })}
                      />
                    </div>
                  </div>
                </>
              )}
            </div>

            <button
              type="submit"
              className="auth-submit-btn"
              disabled={isSubmitting}
              id="register-submit-btn"
              style={{ marginTop: '8px' }}
            >
              {isSubmitting ? (
                <span className="auth-btn-loading">
                  <span className="auth-spinner" />
                  Creating account...
                </span>
              ) : (
                'Create Account'
              )}
            </button>
          </form>

          <div className="auth-divider"><span>Already have an account?</span></div>

          <p className="auth-form-footer">
            <Link to="/login" className="auth-link" id="go-to-login-link">
              Sign in instead
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;