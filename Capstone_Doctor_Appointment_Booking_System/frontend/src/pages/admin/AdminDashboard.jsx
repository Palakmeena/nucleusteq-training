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
import PageHeader from '../../components/common/PageHeader';
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
    <Box>
      <PageHeader title="System Overview" subtitle="Monitor platform health and activity" />

      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <DashboardCard title="Total Doctors" value={stats?.total_doctors || 0} icon={<DoctorsIcon />} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <DashboardCard title="Active Doctors" value={stats?.active_doctors || 0} icon={<ActiveIcon />} color="success" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <DashboardCard title="Patients" value={totalPatients} icon={<PatientsIcon />} color="info" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <DashboardCard title="Appointments" value={stats?.total_appointments || 0} icon={<AppointmentsIcon />} color="warning" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <DashboardCard title="Completed" value={stats?.completed_appointments || 0} icon={<CompletedIcon />} color="success" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2 }}>
          <DashboardCard title="Cancelled" value={stats?.cancelled_appointments || 0} icon={<CancelledIcon />} color="error" />
        </Grid>
      </Grid>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <Button component={Link} to="/admin/doctors" startIcon={<DoctorsIcon />}>
              Manage Doctors
            </Button>
            <Button component={Link} to="/admin/patients" variant="outlined" startIcon={<PatientsIcon />}>
              View Patients
            </Button>
          </Stack>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h6" fontWeight={700} gutterBottom>
            Recent Doctor Registrations
          </Typography>
          {doctors.map((d) => (
            <Stack key={d.id} direction="row" spacing={2} alignItems="center" py={1.5}>
              <Avatar sx={{ bgcolor: 'primary.light', color: 'primary.main' }}>
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
          ))}
        </CardContent>
      </Card>
    </Box>
  );
};

export default AdminDashboard;
