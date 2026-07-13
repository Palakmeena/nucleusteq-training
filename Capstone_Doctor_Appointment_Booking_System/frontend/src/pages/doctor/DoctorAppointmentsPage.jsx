import React, { useState, useMemo } from 'react';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import { toast } from 'react-toastify';
import PageHeader from '../../components/common/PageHeader';
import AppointmentCard from '../../components/cards/AppointmentCard';
import EmptyState from '../../components/emptyState/EmptyState';
import { ListSkeleton } from '../../components/loading/SkeletonLoader';
import ConfirmationDialog from '../../components/dialogs/ConfirmationDialog';
import { useAppointments } from '../../hooks/useAppointments';
import appointmentApi from '../../api/appointmentApi';
import { showError } from '../../utils/errorHandler';
import { UPCOMING_STATUSES } from '../../constants/appointmentStatus';
import dayjs from 'dayjs';

const DoctorAppointmentsPage = () => {
  const { appointments, loading, refetch } = useAppointments('DOCTOR');
  const [tab, setTab] = useState(0);
  const [cancelModal, setCancelModal] = useState({ open: false, appointmentId: null });

  const filtered = useMemo(() => {
    const today = dayjs().format('YYYY-MM-DD');
    if (tab === 0) return appointments.filter((a) => a.appointment_date === today);
    if (tab === 1) return appointments.filter((a) => UPCOMING_STATUSES.includes(a.status));
    if (tab === 2) return appointments.filter((a) => a.status === 'COMPLETED');
    return appointments.filter((a) => a.status === 'CANCELLED' || a.status === 'NO_SHOW');
  }, [appointments, tab]);

  const updateStatus = async (id, status) => {
    try {
      await appointmentApi.updateStatus(id, status);
      toast.success(`Marked as ${status.replace('_', ' ').toLowerCase()}`);
      refetch();
    } catch (error) {
      showError(error, 'Failed to update appointment');
    }
  };

  const handleCancelConfirm = async () => {
    try {
      await appointmentApi.updateStatus(cancelModal.appointmentId, 'CANCELLED');
      toast.success('Appointment cancelled successfully');
      setCancelModal({ open: false, appointmentId: null });
      refetch();
    } catch (error) {
      showError(error, 'Failed to cancel appointment');
    }
  };

  return (
    <Box>
      <PageHeader title="Appointments" subtitle="View and manage patient visits" />

      <Paper sx={{ mb: 3 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)}>
          <Tab label="Today" />
          <Tab label="Upcoming" />
          <Tab label="Completed" />
          <Tab label="Cancelled" />
        </Tabs>
      </Paper>

      {loading ? (
        <ListSkeleton />
      ) : filtered.length > 0 ? (
        filtered.map((a) => (
          <AppointmentCard
            key={a.id}
            appointment={a}
            view="doctor"
            onCancel={() => setCancelModal({ open: true, appointmentId: a.id })}
            onMarkCompleted={() => updateStatus(a.id, 'COMPLETED')}
            onMarkNoShow={() => updateStatus(a.id, 'NO_SHOW')}
          />
        ))
      ) : (
        <EmptyState title="No appointments" description="Nothing scheduled in this view" />
      )}

      <ConfirmationDialog
        open={cancelModal.open}
        title="Cancel Appointment"
        message="Are you sure you want to cancel this appointment? This action cannot be undone."
        confirmText="Cancel Appointment"
        confirmColor="error"
        onConfirm={handleCancelConfirm}
        onCancel={() => setCancelModal({ open: false, appointmentId: null })}
      />
    </Box>
  );
};

export default DoctorAppointmentsPage;
