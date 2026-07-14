import axios from 'axios';
import { toast } from 'react-toastify';

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

let sessionExpiredHandler = null;

export const setSessionExpiredHandler = (handler) => {
  sessionExpiredHandler = handler;
};

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;

    if (status === 401) {
      localStorage.removeItem('token');
      if (sessionExpiredHandler) {
        sessionExpiredHandler();
      }
      toast.error('Session expired. Please sign in again.');
    }

    return Promise.reject(error);
  }
);

export default api;