import React, { useEffect, useState } from 'react';
import { Box, Card, CardContent, Stack, Avatar, Typography } from '@mui/material';
import { toast } from 'react-toastify';
import PageHeader from '../../components/common/PageHeader';
import StatusChip from '../../components/common/StatusChip';
import Button from '../../components/buttons/Button';
import DataTable from '../../components/tables/DataTable';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import adminApi from '../../api/adminApi';
import { showError } from '../../utils/errorHandler';
import { formatCurrency } from '../../utils/formatters';
import { MedicalServices as DoctorIcon } from '@mui/icons-material';

const AdminDoctorsPage = () => {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionId, setActionId] = useState(null);

  const fetchDoctors = async () => {
    try {
      setLoading(true);
      const res = await adminApi.getDoctors();
      setDoctors(res.data || []);
    } catch (error) {
      showError(error, 'Failed to load doctors');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDoctors();
  }, []);

  const toggleDoctor = async (doctor, activate) => {
    try {
      setActionId(doctor.id);
      if (activate) {
        await adminApi.activateDoctor(doctor.id);
        toast.success(`${doctor.full_name} activated`);
      } else {
        await adminApi.deactivateDoctor(doctor.id);
        toast.success(`${doctor.full_name} deactivated`);
      }
      fetchDoctors();
    } catch (error) {
      showError(error, 'Action failed');
    } finally {
      setActionId(null);
    }
  };

  const columns = [
    {
      id: 'name',
      label: 'Doctor',
      render: (_, row) => (
        <Stack direction="row" spacing={1.5} alignItems="center">
          <Avatar sx={{ width: 36, height: 36, bgcolor: 'primary.light', color: 'primary.main' }}>
            <DoctorIcon fontSize="small" />
          </Avatar>
          <Box>
            <Typography variant="body2" fontWeight={600}>
              Dr. {row.full_name}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              {row.phone}
            </Typography>
          </Box>
        </Stack>
      ),
    },
    { id: 'specialization', label: 'Specialty' },
    { id: 'license_number', label: 'License' },
    {
      id: 'fee',
      label: 'Fee',
      render: (_, row) => formatCurrency(row.consultation_fee),
    },
    {
      id: 'status',
      label: 'Status',
      render: (_, row) => <StatusChip status={row.is_active ? 'ACTIVE' : 'INACTIVE'} />,
    },
    {
      id: 'actions',
      label: 'Actions',
      render: (_, row) =>
        row.is_active ? (
          <Button
            size="small"
            color="error"
            variant="outlined"
            loading={actionId === row.id}
            onClick={() => toggleDoctor(row, false)}
          >
            Deactivate
          </Button>
        ) : (
          <Button
            size="small"
            loading={actionId === row.id}
            onClick={() => toggleDoctor(row, true)}
          >
            Activate
          </Button>
        ),
    },
  ];

  return (
    <Box>
      <PageHeader title="Doctor Management" subtitle="Approve and manage doctor accounts" />

      {loading ? (
        <LoadingSpinner />
      ) : (
        <Card>
          <CardContent>
            <DataTable columns={columns} data={doctors} emptyMessage="No doctors registered" />
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default AdminDoctorsPage;