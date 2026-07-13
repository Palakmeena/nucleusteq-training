import { useState, useEffect, useCallback } from 'react';
import appointmentApi from '../api/appointmentApi';
import { showError } from '../utils/errorHandler';

export const useAppointments = (role = 'PATIENT') => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchAppointments = useCallback(async () => {
    try {
      setLoading(true);
      let response;
      if (role === 'DOCTOR') {
        response = await appointmentApi.getDoctorAppointments();
      } else if (role === 'ADMIN') {
        response = await appointmentApi.getAdminAppointments();
      } else {
        response = await appointmentApi.getMyAppointments();
      }
      setAppointments(response.data || []);
    } catch (error) {
      showError(error, 'Failed to load appointments');
    } finally {
      setLoading(false);
    }
  }, [role]);

  useEffect(() => {
    fetchAppointments();
  }, [fetchAppointments]);

  return { appointments, loading, refetch: fetchAppointments };
};
