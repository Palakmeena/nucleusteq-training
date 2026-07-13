import React, { useState, useMemo } from 'react';
import { Box, Card, CardContent, Tabs, Tab, Paper } from '@mui/material';
import PageHeader from '../../components/common/PageHeader';
import AppointmentCard from '../../components/cards/AppointmentCard';
import EmptyState from '../../components/emptyState/EmptyState';
import { ListSkeleton } from '../../components/loading/SkeletonLoader';
import { useAppointments } from '../../hooks/useAppointments';
import dayjs from 'dayjs';

const AdminAppointmentsPage = () => {
  const { appointments, loading } = useAppointments('ADMIN');
  const [tab, setTab] = useState(0);

  const filtered = useMemo(() => {
    const today = dayjs().format('YYYY-MM-DD');
    if (tab === 0) return appointments.filter((a) => a.appointment_date === today);
    if (tab === 1) return appointments.filter((a) => a.appointment_date > today);
    if (tab === 2) return appointments.filter((a) => a.appointment_date < today);
    return appointments;
  }, [appointments, tab]);

  return (
    <Box>
      <PageHeader
        title="Appointment Monitoring"
        subtitle="Platform-wide appointment overview"
      />

      <Paper sx={{ mb: 3 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)}>
          <Tab label="Today" />
          <Tab label="Upcoming" />
          <Tab label="Past" />
          <Tab label="All" />
        </Tabs>
      </Paper>

      {loading ? (
        <ListSkeleton />
      ) : filtered.length > 0 ? (
        filtered.map((a) => (
          <AppointmentCard
            key={a.id}
            appointment={a}
            view="admin"
            patientLabel={`Patient: ${a.patient_name || a.patient_id?.slice(-6)}`}
            doctorName={`Dr. ${a.doctor_name || a.doctor_id?.slice(-6)}`}
          />
        ))
      ) : (
        <EmptyState title="No appointments" description="Nothing found for this filter" />
      )}
    </Box>
  );
};

export default AdminAppointmentsPage;
