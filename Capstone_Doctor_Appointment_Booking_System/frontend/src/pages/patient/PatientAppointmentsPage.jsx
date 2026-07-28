import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import { toast } from 'react-toastify';
import PageHeader from '../../components/common/PageHeader';
import AppointmentCard from '../../components/cards/AppointmentCard';
import ConfirmationDialog from '../../components/dialogs/ConfirmationDialog';
import PaymentModal from '../../components/dialogs/PaymentModal';
import EmptyState from '../../components/emptyState/EmptyState';
import Button from '../../components/buttons/Button';
import { ListSkeleton } from '../../components/loading/SkeletonLoader';
import { useAppointments } from '../../hooks/useAppointments';
import appointmentApi from '../../api/appointmentApi';
import doctorApi from '../../api/doctorApi';
import { showError } from '../../utils/errorHandler';
import { UPCOMING_STATUSES } from '../../constants/appointmentStatus';

const PatientAppointmentsPage = () => {
  const navigate = useNavigate();
  const { appointments, loading, refetch } = useAppointments('PATIENT');
  const [tab, setTab] = useState(0);
  const [cancelId, setCancelId] = useState(null);
  const [paymentId, setPaymentId] = useState(null);
  const [paying, setPaying] = useState(false);
  const [doctorsCache, setDoctorsCache] = useState({});

  useEffect(() => {
    appointments.forEach(async (a) => {
      if (doctorsCache[a.doctor_id]) return;
      try {
        const res = await doctorApi.getDoctorById(a.doctor_id);
        setDoctorsCache((prev) => ({ ...prev, [a.doctor_id]: res.data }));
      } catch {
        // ignore
      }
    });
  }, [appointments, doctorsCache]);

  const filtered = useMemo(() => {
    if (tab === 0) return appointments.filter((a) => UPCOMING_STATUSES.includes(a.status));
    if (tab === 1) return appointments.filter((a) => a.status === 'COMPLETED');
    return appointments.filter((a) => a.status === 'CANCELLED');
  }, [appointments, tab]);

  const handleCancel = async () => {
    try {
      await appointmentApi.cancel(cancelId);
      toast.success('Appointment cancelled');
      setCancelId(null);
      refetch();
    } catch (error) {
      showError(error, 'Failed to cancel appointment');
    }
  };

  const handlePay = async () => {
    try {
      setPaying(true);
      await appointmentApi.pay(paymentId);
      toast.success('Payment successful!');
      setPaymentId(null);
      refetch();
    } catch (error) {
      showError(error, 'Payment failed');
    } finally {
      setPaying(false);
    }
  };

  const counts = {
    upcoming: appointments.filter((a) => UPCOMING_STATUSES.includes(a.status)).length,
    completed: appointments.filter((a) => a.status === 'COMPLETED').length,
    cancelled: appointments.filter((a) => a.status === 'CANCELLED').length,
  };

  return (
    <Box>
      <PageHeader title="My Appointments" subtitle="View, pay, or cancel your bookings" />

      <Paper sx={{ mb: 3 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)}>
          <Tab label={`Upcoming (${counts.upcoming})`} />
          <Tab label={`Completed (${counts.completed})`} />
          <Tab label={`Cancelled (${counts.cancelled})`} />
        </Tabs>
      </Paper>

      {loading ? (
        <ListSkeleton />
      ) : filtered.length > 0 ? (
        filtered.map((a) => (
          <AppointmentCard
            key={a.id}
            appointment={a}
            onCancel={() => setCancelId(a.id)}
            actions={
              a.status === 'PENDING' && a.payment_status === 'PENDING' ? (
                <Button size="small" onClick={() => setPaymentId(a.id)}>
                  Pay Now
                </Button>
              ) : null
            }
          />
        ))
      ) : (
        <EmptyState title="No appointments" description="Nothing in this category yet" />
      )}

      <ConfirmationDialog
        open={!!cancelId}
        onClose={() => setCancelId(null)}
        onConfirm={handleCancel}
        title="Cancel Appointment"
        message="Are you sure? Cancellations within 2 hours of the appointment may not be allowed."
        confirmText="Cancel Appointment"
        severity="warning"
      />
      <PaymentModal
        open={!!paymentId}
        onClose={() => setPaymentId(null)}
        appointment={appointments.find((a) => a.id === paymentId)}
        doctor={paymentId ? doctorsCache[appointments.find((a) => a.id === paymentId)?.doctor_id] : null}
        onSuccess={() => {
          setPaymentId(null);
          refetch();
        }}
      />
    </Box>
  );
};

export default PatientAppointmentsPage;
