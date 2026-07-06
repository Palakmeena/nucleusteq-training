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
import PageHeader from '../../components/common/PageHeader';
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
    <Box>
      <PageHeader
        title={`Welcome${user?.fullName ? `, ${user.fullName.split(' ')[0]}` : ''}!`}
        subtitle="Here's an overview of your healthcare activity"
      />

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

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" fontWeight={700} gutterBottom>
            Quick Actions
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <Button component={Link} to="/doctors" startIcon={<SearchIcon />}>
              Find Doctors
            </Button>
            <Button component={Link} to="/appointments" variant="outlined" startIcon={<CalendarIcon />}>
              My Appointments
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
              {recent.length > 0 ? (
                recent.map((a) => (
                  <AppointmentCard
                    key={a.id}
                    appointment={a}
                    doctorName={doctorNames[a.doctor_id] ? `Dr. ${doctorNames[a.doctor_id]}` : undefined}
                  />
                ))
              ) : (
                <EmptyState
                  title="No appointments yet"
                  description="Book your first appointment with a verified doctor"
                  action
                  actionText="Find Doctors"
                  onAction={() => navigate('/doctors')}
                />
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Stack spacing={2} alignItems="center">
                <Avatar sx={{ width: 72, height: 72, bgcolor: 'primary.main' }}>
                  {(user?.fullName || user?.email || 'P').charAt(0)}
                </Avatar>
                <Typography fontWeight={700}>{user?.fullName || user?.email}</Typography>
                <Divider flexItem />
                <Typography variant="body2" color="text.secondary">
                  Patient account
                </Typography>
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default PatientDashboard;
