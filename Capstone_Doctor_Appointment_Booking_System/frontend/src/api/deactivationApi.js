import api from './axios';

const deactivationApi = {
  // Doctor: submit a leave/deactivation request
  submitRequest: (data) => api.post('/doctors/deactivation/request', data),

  // Doctor: get own deactivation requests
  getMyRequests: () => api.get('/doctors/deactivation/requests'),

  // Doctor: reactivate self (no admin approval needed)
  reactivateSelf: () => api.patch('/doctors/deactivation/reactivate'),
};

export default deactivationApi;
