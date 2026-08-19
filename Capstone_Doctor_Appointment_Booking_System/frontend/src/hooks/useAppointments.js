import { useState, useEffect, useCallback } from 'react';
import appointmentApi from '../api/appointmentApi';
import { showError } from '../utils/errorHandler';
import { useSocketEvent } from './useSocketEvent';
import { SOCKET_EVENTS } from '../constants/socketEvents';

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

  useSocketEvent(SOCKET_EVENTS.APPOINTMENT_CREATED, fetchAppointments);
  useSocketEvent(SOCKET_EVENTS.APPOINTMENT_UPDATED, fetchAppointments);
  useSocketEvent(SOCKET_EVENTS.APPOINTMENT_CANCELLED, fetchAppointments);

  return { appointments, loading, refetch: fetchAppointments };
};
