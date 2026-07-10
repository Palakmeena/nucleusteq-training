import api from './axios';

const authApi = {
  login: (credentials) => api.post('/auth/login', credentials),

  registerPatient: (data) => api.post('/auth/register/patient', data),

  registerDoctor: (data) => api.post('/auth/register/doctor', data),
};

export default authApi;