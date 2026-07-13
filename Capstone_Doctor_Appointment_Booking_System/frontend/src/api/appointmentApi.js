import api from './axios';

const appointmentApi = {
  book: (data) => api.post('/appointments', data),

  pay: (appointmentId) => api.post(`/appointments/${appointmentId}/pay`),

  cancel: (appointmentId) => api.delete(`/appointments/${appointmentId}`),

  getMyAppointments: () => api.get('/appointments/patient'),

  getDoctorAppointments: () => api.get('/appointments/doctor'),

  getAdminAppointments: () => api.get('/appointments/admin'),

  updateStatus: (appointmentId, status) =>
    api.patch(`/appointments/${appointmentId}/status`, { status }),
};

export default appointmentApi;