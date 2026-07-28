import api from './axios';

const doctorApi = {
  getDoctors: (params) => api.get('/doctors', { params }),

  getDoctorById: (id) => api.get(`/doctors/${id}`),

  getProfile: () => api.get('/doctors/profile'),

  updateProfile: (data) => api.put('/doctors/profile', data),
};

export default doctorApi;
