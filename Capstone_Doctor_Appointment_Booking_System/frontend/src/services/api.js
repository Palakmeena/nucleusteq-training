import axios from 'axios';

// Using Vite proxy — requests are forwarded to the real service, avoiding CORS
// /user-api → http://localhost:8001 (User Service)
// /appointment-api → http://localhost:8002 (Appointment Service)

export const authApi = axios.create({
  baseURL: '/user-api/api/v1',
});

// The Appointment Service
export const appointmentApi = axios.create({
  baseURL: '/appointment-api/api/v1',
});


// Interceptor to add JWT token to every request for the appointment service
appointmentApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// Interceptor for User service to add token if needed (e.g., getting profile)
authApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// ─── Auth Service Functions ───────────────────────────────────────────────────
export const authService = {
  /**
   * POST /auth/login
   * @param {{ email: string, password: string }} credentials
   */
  login: (credentials) => authApi.post('/auth/login', credentials),

  /**
   * POST /auth/register/patient
   * Required fields: full_name, email, password, phone, gender, date_of_birth
   */
  registerPatient: (data) => authApi.post('/auth/register/patient', data),

  /**
   * POST /auth/register/doctor
   * Required fields: full_name, email, password, phone, qualification,
   * experience, license_number, specialization, consultation_fee, clinic_address
   */
  registerDoctor: (data) => authApi.post('/auth/register/doctor', data),
};
