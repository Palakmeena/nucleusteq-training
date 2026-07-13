import api from './axios';

const adminApi = {
  getDashboard: () => api.get('/admin/dashboard'),

  getUsers: () => api.get('/admin/users'),

  getDoctors: () => api.get('/admin/doctors'),

  // ── Part 1: Registration Approval ────────────────────────────────────────
  approveDoctor: (doctorId) => api.patch(`/admin/doctors/${doctorId}/approve`),

  rejectDoctor: (doctorId) => api.patch(`/admin/doctors/${doctorId}/reject`),

  // ── Part 2: Deactivation Request Management ──────────────────────────────
  getDeactivationRequests: () => api.get('/admin/deactivation-requests'),

  approveDeactivationRequest: (requestId) =>
    api.patch(`/admin/deactivation-requests/${requestId}/approve`),

  rejectDeactivationRequest: (requestId) =>
    api.patch(`/admin/deactivation-requests/${requestId}/reject`),

  getRecentAppointments: () => api.get('/admin/appointments/recent'),
};

export default adminApi;
