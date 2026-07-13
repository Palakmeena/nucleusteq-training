import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Box, Grid, Card, CardContent, Stack, Avatar, Typography } from '@mui/material';
import {
  MedicalServices as DoctorsIcon,
  People as PatientsIcon,
  CalendarMonth as AppointmentsIcon,
  CheckCircle as CompletedIcon,
  Cancel as CancelledIcon,
  EventAvailable as ActiveIcon,
} from '@mui/icons-material';
import DashboardCard from '../../components/cards/DashboardCard';
import StatusChip from '../../components/common/StatusChip';
import Button from '../../components/buttons/Button';
import { DashboardSkeleton } from '../../components/loading/SkeletonLoader';
import adminApi from '../../api/adminApi';
import { showError } from '../../utils/errorHandler';

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [doctors, setDoctors] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [dashRes, doctorsRes, apptsRes] = await Promise.all([
          adminApi.getDashboard(),
          adminApi.getDoctors(),
          adminApi.getRecentAppointments(),
        ]);
        setStats(dashRes.data);
        setDoctors((doctorsRes.data || []).slice(0, 5));
        setAppointments(apptsRes.data || []);
      } catch (error) {
        showError(error, 'Failed to load dashboard');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <DashboardSkeleton />;

  const totalPatients = (stats?.total_users || 0) - (stats?.total_doctors || 0);

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" fontWeight={700} mb={1}>
        System Overview
      </Typography>
      <Typography variant="body2" color="text.secondary" mb={4}>
        Monitor platform health and activity
      </Typography>

      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <DashboardCard title="Total Doctors" value={stats?.total_doctors || 0} icon={<DoctorsIcon />} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <DashboardCard title="Active Doctors" value={stats?.active_doctors || 0} icon={<ActiveIcon />} color="success" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <DashboardCard title="Patients" value={totalPatients} icon={<PatientsIcon />} color="info" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <DashboardCard title="Appointments" value={stats?.total_appointments || 0} icon={<AppointmentsIcon />} color="warning" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <DashboardCard title="Completed" value={stats?.completed_appointments || 0} icon={<CompletedIcon />} color="success" />
        </Grid>
      </Grid>

      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, md: 12 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" fontWeight={600} gutterBottom mb={3}>
                Quick Actions
              </Typography>
              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Button
                    component={Link}
                    to="/admin/doctors"
                    startIcon={<DoctorsIcon />}
                    fullWidth
                    sx={{
                      py: 2.5,
                      borderRadius: 2,
                      textTransform: 'none',
                      fontWeight: 600,
                    }}
                  >
                    Manage Doctors
                  </Button>
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Button
                    component={Link}
                    to="/admin/patients"
                    variant="outlined"
                    startIcon={<PatientsIcon />}
                    fullWidth
                    sx={{
                      py: 2.5,
                      borderRadius: 2,
                      textTransform: 'none',
                      fontWeight: 600,
                    }}
                  >
                    View Patients
                  </Button>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" fontWeight={600} gutterBottom mb={4}>
                Recent Doctor Registrations
              </Typography>
              {doctors.length > 0 ? (
                <Stack spacing={2}>
                  {doctors.map((d) => (
                    <Card
                      key={d.id}
                      sx={{
                        p: 2,
                        borderRadius: 2,
                        border: '1px solid',
                        borderColor: 'divider',
                        transition: 'all 0.2s',
                        '&:hover': { borderColor: 'primary.main', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' },
                      }}
                    >
                      <Stack direction="row" spacing={2} alignItems="center">
                        <Avatar sx={{ bgcolor: 'primary.light', color: 'primary.main', width: 48, height: 48 }}>
                          <DoctorsIcon />
                        </Avatar>
                        <Stack flex={1}>
                          <Typography fontWeight={600}>Dr. {d.full_name}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            {d.specialization} · {d.license_number}
                          </Typography>
                        </Stack>
                        <StatusChip status={d.is_active ? 'ACTIVE' : 'INACTIVE'} />
                      </Stack>
                    </Card>
                  ))}
                </Stack>
              ) : (
                <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
                  No recent doctor registrations
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 6 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" fontWeight={600} gutterBottom mb={4}>
                Recent Appointments
              </Typography>
              {appointments.length > 0 ? (
                <Stack spacing={2}>
                  {appointments.map((a) => (
                    <Card
                      key={a.id}
                      sx={{
                        p: 2,
                        borderRadius: 2,
                        border: '1px solid',
                        borderColor: 'divider',
                        transition: 'all 0.2s',
                        '&:hover': { borderColor: 'primary.main', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' },
                      }}
                    >
                      <Stack direction="row" spacing={2} alignItems="center">
                        <Avatar sx={{ bgcolor: 'info.light', color: 'info.main', width: 48, height: 48 }}>
                          <AppointmentsIcon />
                        </Avatar>
                        <Stack flex={1}>
                          <Typography fontWeight={600}>Appointment #{a.id?.slice(-6)}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            {a.appointment_date} · Doctor: {a.doctor_id?.slice(-6)}
                          </Typography>
                        </Stack>
                        <StatusChip status={a.status} />
                      </Stack>
                    </Card>
                  ))}
                </Stack>
              ) : (
                <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
                  No recent appointments
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AdminDashboard;
