import api from './axios';

const adminApi = {
  getDashboard: () => api.get('/admin/dashboard'),

  getUsers: () => api.get('/admin/users'),

  getDoctors: () => api.get('/admin/doctors'),

  activateDoctor: (doctorId) => api.patch(`/admin/doctors/${doctorId}/activate`),

  deactivateDoctor: (doctorId) => api.patch(`/admin/doctors/${doctorId}/deactivate`),

  getRecentAppointments: () => api.get('/admin/appointments/recent'),
};

export default adminApi;