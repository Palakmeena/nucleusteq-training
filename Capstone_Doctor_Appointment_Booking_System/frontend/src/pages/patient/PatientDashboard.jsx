import React, { useMemo, useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Box, Grid, Card, CardContent, Stack, Avatar, Typography, Divider } from '@mui/material';
import {
  EventAvailable as UpcomingIcon,
  CheckCircle as CompletedIcon,
  Cancel as CancelledIcon,
  Search as SearchIcon,
  CalendarMonth as CalendarIcon,
} from '@mui/icons-material';
import { useAuth } from '../../context/AuthContext';
import DashboardCard from '../../components/cards/DashboardCard';
import AppointmentCard from '../../components/cards/AppointmentCard';
import Button from '../../components/buttons/Button';
import { DashboardSkeleton } from '../../components/loading/SkeletonLoader';
import EmptyState from '../../components/emptyState/EmptyState';
import { useAppointments } from '../../hooks/useAppointments';
import { UPCOMING_STATUSES } from '../../constants/appointmentStatus';
import doctorApi from '../../api/doctorApi';

const PatientDashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { appointments, loading } = useAppointments('PATIENT');
  const [doctorNames, setDoctorNames] = useState({});

  useEffect(() => {
    const ids = [...new Set(appointments.map((a) => a.doctor_id))];
    ids.forEach(async (id) => {
      try {
        const res = await doctorApi.getDoctorById(id);
        setDoctorNames((prev) => ({ ...prev, [id]: res.data.full_name }));
      } catch {
        // ignore
      }
    });
  }, [appointments]);

  const stats = useMemo(
    () => ({
      upcoming: appointments.filter((a) => UPCOMING_STATUSES.includes(a.status)).length,
      completed: appointments.filter((a) => a.status === 'COMPLETED').length,
      cancelled: appointments.filter((a) => a.status === 'CANCELLED').length,
    }),
    [appointments]
  );

  const recent = appointments.slice(0, 5);

  if (loading) return <DashboardSkeleton />;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" fontWeight={700} mb={1}>
        Welcome{user?.fullName ? `, ${user.fullName.split(' ')[0]}` : ''}!
      </Typography>
      <Typography variant="body2" color="text.secondary" mb={4}>
        Here's an overview of your healthcare activity
      </Typography>

      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <DashboardCard title="Upcoming" value={stats.upcoming} icon={<UpcomingIcon />} color="primary" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <DashboardCard title="Completed" value={stats.completed} icon={<CompletedIcon />} color="success" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <DashboardCard title="Cancelled" value={stats.cancelled} icon={<CancelledIcon />} color="error" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <DashboardCard
            title="Total"
            value={appointments.length}
            icon={<CalendarIcon />}
            color="info"
          />
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
                    to="/find-doctors"
                    startIcon={<SearchIcon />}
                    fullWidth
                    sx={{
                      py: 2.5,
                      borderRadius: 2,
                      textTransform: 'none',
                      fontWeight: 600,
                    }}
                  >
                    Find Doctors
                  </Button>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Button
                    component={Link}
                    to="/appointments"
                    variant="outlined"
                    startIcon={<CalendarIcon />}
                    fullWidth
                    sx={{
                      py: 2.5,
                      borderRadius: 2,
                      textTransform: 'none',
                      fontWeight: 600,
                    }}
                  >
                    My Appointments
                  </Button>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ height: '100%', background: 'linear-gradient(135deg, #9c27b0 0%, #7b1fa2 100%)' }}>
            <CardContent>
              <Typography variant="h6" fontWeight={600} color="white" gutterBottom mb={2}>
                Health Journey
              </Typography>
              <Typography variant="body2" color="rgba(255,255,255,0.9)" mb={3}>
                Track your healthcare progress and stay on top of your appointments
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
              {recent.length > 0 ? (
                <Stack spacing={2}>
                  {recent.map((a) => (
                    <AppointmentCard
                      key={a.id}
                      appointment={a}
                      doctorName={doctorNames[a.doctor_id] ? `Dr. ${doctorNames[a.doctor_id]}` : undefined}
                    />
                  ))}
                </Stack>
              ) : (
                <EmptyState
                  title="No appointments yet"
                  description="Book your first appointment with a verified doctor"
                  action
                  actionText="Find Doctors"
                  onAction={() => navigate('/find-doctors')}
                />
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Stack spacing={3} alignItems="center">
                <Avatar sx={{ width: 80, height: 80, bgcolor: 'primary.main', fontSize: '2rem' }}>
                  {(user?.fullName || user?.email || 'P').charAt(0)}
                </Avatar>
                <Typography fontWeight={600} variant="h6" textAlign="center">
                  {user?.fullName || user?.email}
                </Typography>
                <Divider flexItem sx={{ width: '100%' }} />
                <Box sx={{ width: '100%' }}>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Total Appointments
                  </Typography>
                  <Typography variant="h4" fontWeight={700} color="primary.main">
                    {appointments.length}
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

export default PatientDashboard;