import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { authService } from '../../services/api';
import './AuthPages.css';

const RegisterPage = () => {
  const [role, setRole] = useState('PATIENT');
  const [formData, setFormData] = useState({
    full_name: '',
    email: '',
    password: '',
    phone: '',
    gender: '',
    date_of_birth: '',
    qualification: '',
    experience: '',
    license_number: '',
    specialization: '',
    consultation_fee: '',
    clinic_address: '',
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (role === 'PATIENT') {
        // Patient requires: full_name, email, password, phone, gender, date_of_birth
        await authService.registerPatient({
          full_name: formData.full_name,
          email: formData.email,
          password: formData.password,
          phone: formData.phone,
          gender: formData.gender,
          date_of_birth: formData.date_of_birth,
        });
      } else {
        // Doctor requires: full_name, email, password, phone, and professional fields
        await authService.registerDoctor({
          full_name: formData.full_name,
          email: formData.email,
          password: formData.password,
          phone: formData.phone,
          qualification: formData.qualification,
          experience: Number(formData.experience),
          license_number: formData.license_number,
          specialization: formData.specialization,
          consultation_fee: Number(formData.consultation_fee),
          clinic_address: formData.clinic_address,
        });
      }

      if (role === 'DOCTOR') {
        toast.info('Doctor registration submitted. Admin will verify your license and activate your account.');
        navigate('/login', {
          state: {
            message: 'Your doctor profile has been submitted. Admin will review your license and approve the account before you can log in.',
          },
        });
      } else {
        toast.success('Registration successful! Please sign in.');
        navigate('/login');
      }
    } catch (error) {
      // Extract validation errors from FastAPI Pydantic response
      const detail = error.response?.data?.detail;
      if (Array.isArray(detail)) {
        const msg = detail.map(d => d.msg).join(', ');
        toast.error(msg);
      } else {
        toast.error(detail || 'Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page-container">
      <div className="auth-card register-card">
        <div className="auth-header">
          <div className="auth-logo">MedPulse</div>
          <h2>Create an account</h2>
          <p>Join MedPulse to manage your healthcare journey.</p>
        </div>

        {/* Role Toggle */}
        <div className="role-toggle">
          <button
            type="button"
            className={`role-btn ${role === 'PATIENT' ? 'active' : ''}`}
            onClick={() => setRole('PATIENT')}
          >
            I'm a Patient
          </button>
          <button
            type="button"
            className={`role-btn ${role === 'DOCTOR' ? 'active' : ''}`}
            onClick={() => setRole('DOCTOR')}
          >
            I'm a Doctor
          </button>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="full_name">Full Name</label>
            <input
              type="text"
              id="full_name"
              name="full_name"
              placeholder="e.g. John Doe"
              value={formData.full_name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="reg-email">Email address</label>
            <input
              type="email"
              id="reg-email"
              name="email"
              placeholder="Enter your email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="reg-password">
              Password <span className="field-hint">(8–12 chars, 1 uppercase, 1 special)</span>
            </label>
            <input
              type="password"
              id="reg-password"
              name="password"
              placeholder="Create a strong password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="phone">Phone Number</label>
            <input
              type="tel"
              id="phone"
              name="phone"
              placeholder="10-digit number"
              value={formData.phone}
              onChange={handleChange}
              required
            />
          </div>

          {/* Patient-only fields */}
          {role === 'PATIENT' && (
            <>
              <div className="form-row">
                <div className="form-group half-width">
                  <label htmlFor="gender">Gender</label>
                  <select id="gender" name="gender" value={formData.gender} onChange={handleChange} className="role-select" required>
                    <option value="">Select</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
                <div className="form-group half-width">
                  <label htmlFor="date_of_birth">Date of Birth</label>
                  <input
                    type="date"
                    id="date_of_birth"
                    name="date_of_birth"
                    value={formData.date_of_birth}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>
            </>
          )}

          {role === 'DOCTOR' && (
            <>
              <div className="form-row">
                <div className="form-group half-width">
                  <label htmlFor="qualification">Qualification</label>
                  <input
                    type="text"
                    id="qualification"
                    name="qualification"
                    placeholder="e.g. MBBS, MD"
                    value={formData.qualification}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group half-width">
                  <label htmlFor="experience">Experience (years)</label>
                  <input
                    type="number"
                    id="experience"
                    name="experience"
                    min="0"
                    placeholder="e.g. 5"
                    value={formData.experience}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group half-width">
                  <label htmlFor="license_number">License Number</label>
                  <input
                    type="text"
                    id="license_number"
                    name="license_number"
                    placeholder="e.g. LIC123456"
                    value={formData.license_number}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group half-width">
                  <label htmlFor="specialization">Specialization</label>
                  <select
                    id="specialization"
                    name="specialization"
                    value={formData.specialization}
                    onChange={handleChange}
                    className="role-select"
                    required
                  >
                    <option value="">Select</option>
                    <option value="Cardiologist">Cardiologist</option>
                    <option value="Dermatologist">Dermatologist</option>
                    <option value="Dentist">Dentist</option>
                    <option value="Neurologist">Neurologist</option>
                    <option value="Orthopedic">Orthopedic</option>
                    <option value="General Physician">General Physician</option>
                    <option value="Pediatrician">Pediatrician</option>
                    <option value="Gynecologist">Gynecologist</option>
                    <option value="Psychiatrist">Psychiatrist</option>
                    <option value="Ophthalmologist">Ophthalmologist</option>
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group half-width">
                  <label htmlFor="consultation_fee">Consultation Fee</label>
                  <input
                    type="number"
                    id="consultation_fee"
                    name="consultation_fee"
                    min="1"
                    step="0.01"
                    placeholder="e.g. 500"
                    value={formData.consultation_fee}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group half-width">
                  <label htmlFor="clinic_address">Clinic Address</label>
                  <input
                    type="text"
                    id="clinic_address"
                    name="clinic_address"
                    placeholder="e.g. 123 Main Street"
                    value={formData.clinic_address}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              <div className="doctor-notice">
                🩺 Your complete profile will be reviewed and activated by an admin once.
              </div>
            </>
          )}

          <button type="submit" className="btn btn-primary auth-submit-btn" disabled={loading}>
            {loading ? 'Registering...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          <p>Already have an account? <Link to="/login">Sign in</Link></p>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
