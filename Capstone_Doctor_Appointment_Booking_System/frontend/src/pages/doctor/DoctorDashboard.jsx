import React, { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { Box, Grid, Card, CardContent, Stack, Avatar, Typography, Divider } from '@mui/material';
import {
  EventAvailable as UpcomingIcon,
  CheckCircle as CompletedIcon,
  Cancel as CancelledIcon,
  CalendarMonth as CalendarIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import dayjs from 'dayjs';
import { useAuth } from '../../context/AuthContext';
import PageHeader from '../../components/common/PageHeader';
import DashboardCard from '../../components/cards/DashboardCard';
import AppointmentCard from '../../components/cards/AppointmentCard';
import Button from '../../components/buttons/Button';
import { DashboardSkeleton } from '../../components/loading/SkeletonLoader';
import EmptyState from '../../components/emptyState/EmptyState';
import { useAppointments } from '../../hooks/useAppointments';
import { UPCOMING_STATUSES } from '../../constants/appointmentStatus';

const DoctorDashboard = () => {
  const { user } = useAuth();
  const { appointments, loading } = useAppointments('DOCTOR');

  const stats = useMemo(() => {
    const today = dayjs().format('YYYY-MM-DD');
    return {
      today: appointments.filter(
        (a) => a.appointment_date === today && UPCOMING_STATUSES.includes(a.status)
      ).length,
      upcoming: appointments.filter((a) => UPCOMING_STATUSES.includes(a.status)).length,
      completed: appointments.filter((a) => a.status === 'COMPLETED').length,
      cancelled: appointments.filter(
        (a) => a.status === 'CANCELLED' || a.status === 'NO_SHOW'
      ).length,
    };
  }, [appointments]);

  if (loading) return <DashboardSkeleton />;

  return (
    <Box>
      <PageHeader title="Doctor Dashboard" subtitle="Manage your practice at a glance" />

      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <DashboardCard title="Today" value={stats.today} icon={<CalendarIcon />} color="primary" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <DashboardCard title="Upcoming" value={stats.upcoming} icon={<UpcomingIcon />} color="info" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <DashboardCard title="Completed" value={stats.completed} icon={<CompletedIcon />} color="success" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <DashboardCard title="Cancelled" value={stats.cancelled} icon={<CancelledIcon />} color="error" />
        </Grid>
      </Grid>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" fontWeight={700} gutterBottom>
            Quick Actions
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <Button component={Link} to="/doctor/slots" startIcon={<AddIcon />}>
              Add Slot
            </Button>
            <Button component={Link} to="/doctor/appointments" variant="outlined">
              View Appointments
            </Button>
          </Stack>
        </CardContent>
      </Card>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={700} gutterBottom>
                Recent Appointments
              </Typography>
              {appointments.slice(0, 5).length > 0 ? (
                appointments.slice(0, 5).map((a) => (
                  <AppointmentCard key={a.id} appointment={a} view="doctor" />
                ))
              ) : (
                <EmptyState title="No appointments yet" description="Add slots to start receiving bookings" />
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Stack spacing={2} alignItems="center">
                <Avatar sx={{ width: 72, height: 72, bgcolor: 'primary.main' }}>
                  {(user?.fullName || 'D').charAt(0)}
                </Avatar>
                <Typography fontWeight={700}>{user?.fullName || user?.email}</Typography>
                <Divider flexItem />
                <Typography variant="body2" color="text.secondary">
                  {stats.completed} completed visits
                </Typography>
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DoctorDashboard;
