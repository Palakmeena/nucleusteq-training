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
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" fontWeight={700} mb={1}>
        Doctor Dashboard
      </Typography>
      <Typography variant="body2" color="text.secondary" mb={4}>
        Manage your practice at a glance
      </Typography>

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

      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" fontWeight={600} gutterBottom mb={3}>
                Quick Actions
              </Typography>
              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Button
                    component={Link}
                    to="/doctor/slots"
                    startIcon={<AddIcon />}
                    fullWidth
                    sx={{
                      py: 2.5,
                      borderRadius: 2,
                      textTransform: 'none',
                      fontWeight: 600,
                    }}
                  >
                    Add Slot
                  </Button>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Button
                    component={Link}
                    to="/doctor/appointments"
                    variant="outlined"
                    fullWidth
                    sx={{
                      py: 2.5,
                      borderRadius: 2,
                      textTransform: 'none',
                      fontWeight: 600,
                    }}
                  >
                    View Appointments
                  </Button>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ height: '100%', background: 'linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%)' }}>
            <CardContent>
              <Typography variant="h6" fontWeight={600} color="white" gutterBottom mb={2}>
                Practice Summary
              </Typography>
              <Typography variant="body2" color="rgba(255,255,255,0.9)" mb={3}>
                Your practice is performing well this month
              </Typography>
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Box sx={{ bgcolor: 'rgba(255,255,255,0.2)', px: 2, py: 1, borderRadius: 1 }}>
                  <Typography variant="caption" color="white">Completed</Typography>
                  <Typography variant="subtitle2" fontWeight={600} color="white">{stats.completed}</Typography>
                </Box>
                <Box sx={{ bgcolor: 'rgba(255,255,255,0.2)', px: 2, py: 1, borderRadius: 1 }}>
                  <Typography variant="caption" color="white">Upcoming</Typography>
                  <Typography variant="subtitle2" fontWeight={600} color="white">{stats.upcoming}</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={600} gutterBottom mb={4}>
                Recent Appointments
              </Typography>
              {appointments.slice(0, 5).length > 0 ? (
                <Stack spacing={2}>
                  {appointments.slice(0, 5).map((a) => (
                    <AppointmentCard key={a.id} appointment={a} view="doctor" />
                  ))}
                </Stack>
              ) : (
                <EmptyState title="No appointments yet" description="Add slots to start receiving bookings" />
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Stack spacing={3} alignItems="center">
                <Avatar sx={{ width: 80, height: 80, bgcolor: 'primary.main', fontSize: '2rem' }}>
                  {(user?.fullName || 'D').charAt(0)}
                </Avatar>
                <Typography fontWeight={600} variant="h6" textAlign="center">
                  {user?.fullName || user?.email}
                </Typography>
                <Divider flexItem sx={{ width: '100%' }} />
                <Box sx={{ width: '100%' }}>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Total Completed Visits
                  </Typography>
                  <Typography variant="h4" fontWeight={700} color="primary.main">
                    {stats.completed}
                  </Typography>
                </Box>
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DoctorDashboard;
