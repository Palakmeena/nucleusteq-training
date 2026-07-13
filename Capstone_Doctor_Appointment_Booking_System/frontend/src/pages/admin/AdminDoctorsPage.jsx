import React, { useEffect, useState } from 'react';
import {
  Box, Card, CardContent, Stack, Avatar, Typography, Tabs, Tab,
} from '@mui/material';
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

const STATUS_LABELS = {
  PENDING: 'Pending',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
};

const AdminDoctorsPage = () => {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionId, setActionId] = useState(null);
  const [tab, setTab] = useState(0);

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

  const handleApprove = async (doctor) => {
    try {
      setActionId(doctor.id);
      await adminApi.approveDoctor(doctor.id);
      toast.success(`Dr. ${doctor.full_name} has been approved`);
      fetchDoctors();
    } catch (error) {
      showError(error, 'Failed to approve doctor');
    } finally {
      setActionId(null);
    }
  };

  const handleReject = async (doctor) => {
    try {
      setActionId(doctor.id);
      await adminApi.rejectDoctor(doctor.id);
      toast.success(`Dr. ${doctor.full_name} has been rejected`);
      fetchDoctors();
    } catch (error) {
      showError(error, 'Failed to reject doctor');
    } finally {
      setActionId(null);
    }
  };

  const tabStatuses = ['PENDING', 'APPROVED', 'REJECTED'];
  const filtered = doctors.filter((d) => d.status === tabStatuses[tab]);

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
      render: (_, row) => <StatusChip status={row.status} />,
    },
    {
      id: 'actions',
      label: 'Actions',
      render: (_, row) => {
        if (row.status === 'PENDING') {
          return (
            <Stack direction="row" spacing={1}>
              <Button
                size="small"
                color="success"
                loading={actionId === row.id}
                onClick={() => handleApprove(row)}
                id={`approve-doctor-${row.id}`}
              >
                Approve
              </Button>
              <Button
                size="small"
                color="error"
                variant="outlined"
                loading={actionId === row.id}
                onClick={() => handleReject(row)}
                id={`reject-doctor-${row.id}`}
              >
                Reject
              </Button>
            </Stack>
          );
        }
        if (row.status === 'APPROVED') {
          return (
            <Button
              size="small"
              color="error"
              variant="outlined"
              loading={actionId === row.id}
              onClick={() => handleReject(row)}
              id={`reject-approved-doctor-${row.id}`}
            >
              Reject
            </Button>
          );
        }
        return (
          <Button
            size="small"
            loading={actionId === row.id}
            onClick={() => handleApprove(row)}
            id={`approve-rejected-doctor-${row.id}`}
          >
            Approve
          </Button>
        );
      },
    },
  ];

  return (
    <Box>
      <PageHeader
        title="Doctor Registration Approvals"
        subtitle="Review and approve or reject doctor registrations"
      />

      <Card sx={{ mb: 3 }}>
        <Tabs
          value={tab}
          onChange={(_, v) => setTab(v)}
          sx={{ px: 2, borderBottom: '1px solid', borderColor: 'divider' }}
        >
          <Tab
            label={`Pending (${doctors.filter((d) => d.status === 'PENDING').length})`}
            id="tab-pending"
          />
          <Tab
            label={`Approved (${doctors.filter((d) => d.status === 'APPROVED').length})`}
            id="tab-approved"
          />
          <Tab
            label={`Rejected (${doctors.filter((d) => d.status === 'REJECTED').length})`}
            id="tab-rejected"
          />
        </Tabs>
      </Card>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <Card>
          <CardContent>
            <DataTable
              columns={columns}
              data={filtered}
              emptyMessage={`No ${STATUS_LABELS[tabStatuses[tab]].toLowerCase()} registrations`}
            />
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default AdminDoctorsPage;
