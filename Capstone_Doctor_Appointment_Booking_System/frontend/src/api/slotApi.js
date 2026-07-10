import api from './axios';

const slotApi = {
  createSlot: (data) => api.post('/slots', data),

  updateSlot: (id, data) => api.put(`/slots/${id}`, data),

  deleteSlot: (id) => api.delete(`/slots/${id}`),

  getSlotsByDoctor: (doctorId) => api.get(`/slots/doctor/${doctorId}`),

  getDoctorSlots: () => api.get('/slots/doctor'),
};

export default slotApi;
