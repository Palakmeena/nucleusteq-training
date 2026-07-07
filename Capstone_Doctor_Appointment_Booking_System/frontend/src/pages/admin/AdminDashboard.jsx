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
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [dashRes, doctorsRes] = await Promise.all([
          adminApi.getDashboard(),
          adminApi.getDoctors(),
        ]);
        setStats(dashRes.data);
        setDoctors((doctorsRes.data || []).slice(0, 5));
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
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ height: '100%', background: 'linear-gradient(135deg, #1976d2 0%, #1565c0 100%)' }}>
            <CardContent>
              <Typography variant="h6" fontWeight={600} color="white" gutterBottom mb={2}>
                Platform Health
              </Typography>
              <Typography variant="body2" color="rgba(255,255,255,0.9)" mb={3}>
                System operating normally with all services active
              </Typography>
              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Box sx={{ bgcolor: 'rgba(255,255,255,0.2)', px: 2, py: 1, borderRadius: 1 }}>
                  <Typography variant="caption" color="white">Uptime</Typography>
                  <Typography variant="subtitle2" fontWeight={600} color="white">99.9%</Typography>
                </Box>
                <Box sx={{ bgcolor: 'rgba(255,255,255,0.2)', px: 2, py: 1, borderRadius: 1 }}>
                  <Typography variant="caption" color="white">Response</Typography>
                  <Typography variant="subtitle2" fontWeight={600} color="white">&lt;100ms</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Card>
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
    </Box>
  );
};

export default AdminDashboard;
