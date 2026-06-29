import axios from 'axios';

// The User Service runs on port 8001
export const authApi = axios.create({
  baseURL: 'http://localhost:8001/api/v1',
});

// The Appointment Service runs on port 8002
export const appointmentApi = axios.create({
  baseURL: 'http://localhost:8002/api/v1',
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
