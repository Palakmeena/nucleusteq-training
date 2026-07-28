import React, { useEffect, useState } from 'react';
import {
  Box, Card, CardContent, Stack, Avatar, Typography,
  Chip, Tabs, Tab,
} from '@mui/material';
import {
  BeachAccess as LeaveIcon,
  CheckCircle as ApproveIcon,
  Cancel as RejectIcon,
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import PageHeader from '../../components/common/PageHeader';
import Button from '../../components/buttons/Button';
import DataTable from '../../components/tables/DataTable';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import adminApi from '../../api/adminApi';
import { showError } from '../../utils/errorHandler';

const STATUS_COLOR = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'error',
};

const AdminDeactivationRequestsPage = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionId, setActionId] = useState(null);
  const [tab, setTab] = useState(0);

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const res = await adminApi.getDeactivationRequests();
      setRequests(res.data || []);
    } catch (error) {
      showError(error, 'Failed to load deactivation requests');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const handleApprove = async (req) => {
    try {
      setActionId(req.id);
      await adminApi.approveDeactivationRequest(req.id);
      toast.success(
        `Deactivation approved for Dr. ${req.doctor_name || req.doctor_id}. Unbooked slots removed.`
      );
      fetchRequests();
    } catch (error) {
      showError(error, 'Failed to approve request');
    } finally {
      setActionId(null);
    }
  };

  const handleReject = async (req) => {
    try {
      setActionId(req.id);
      await adminApi.rejectDeactivationRequest(req.id);
      toast.success(`Deactivation request rejected for Dr. ${req.doctor_name || req.doctor_id}`);
      fetchRequests();
    } catch (error) {
      showError(error, 'Failed to reject request');
    } finally {
      setActionId(null);
    }
  };

  const tabStatuses = ['PENDING', 'APPROVED', 'REJECTED'];
  const filtered = requests.filter((r) => r.status === tabStatuses[tab]);

  const columns = [
    {
      id: 'doctor',
      label: 'Doctor',
      render: (_, row) => (
        <Stack direction="row" spacing={1.5} alignItems="center">
          <Avatar sx={{ width: 36, height: 36, bgcolor: 'warning.light', color: 'warning.dark' }}>
            <LeaveIcon fontSize="small" />
          </Avatar>
          <Box>
            <Typography variant="body2" fontWeight={600}>
              Dr. {row.doctor_name || row.doctor_id}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              ID: {row.doctor_id?.slice(-8)}
            </Typography>
          </Box>
        </Stack>
      ),
    },
    {
      id: 'period',
      label: 'Requested Period',
      render: (_, row) => (
        <Box>
          <Typography variant="body2" fontWeight={600}>
            {row.start_date}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            to {row.end_date}
          </Typography>
        </Box>
      ),
    },
    {
      id: 'reason',
      label: 'Reason',
      render: (_, row) => (
        <Typography variant="body2" color={row.reason ? 'text.primary' : 'text.secondary'}>
          {row.reason || '—'}
        </Typography>
      ),
    },
    {
      id: 'submitted',
      label: 'Submitted',
      render: (_, row) =>
        new Date(row.created_at).toLocaleDateString('en-IN', {
          day: '2-digit',
          month: 'short',
          year: 'numeric',
        }),
    },
    {
      id: 'status',
      label: 'Status',
      render: (_, row) => (
        <Chip
          label={row.status}
          color={STATUS_COLOR[row.status] || 'default'}
          size="small"
          sx={{ fontWeight: 700 }}
        />
      ),
    },
    {
      id: 'actions',
      label: 'Actions',
      render: (_, row) => {
        if (row.status !== 'PENDING') {
          return (
            <Typography variant="caption" color="text.secondary">
              {row.status === 'APPROVED' ? 'Approved' : 'Rejected'}
            </Typography>
          );
        }
        return (
          <Stack direction="row" spacing={1}>
            <Button
              size="small"
              color="success"
              startIcon={<ApproveIcon />}
              loading={actionId === row.id}
              onClick={() => handleApprove(row)}
              id={`approve-deactivation-${row.id}`}
            >
              Approve
            </Button>
            <Button
              size="small"
              color="error"
              variant="outlined"
              startIcon={<RejectIcon />}
              loading={actionId === row.id}
              onClick={() => handleReject(row)}
              id={`reject-deactivation-${row.id}`}
            >
              Reject
            </Button>
          </Stack>
        );
      },
    },
  ];

  return (
    <Box>
      <PageHeader
        title="Doctor Leave Requests"
        subtitle="Review and manage doctor temporary deactivation requests"
      />

      <Card sx={{ mb: 3 }}>
        <Tabs
          value={tab}
          onChange={(_, v) => setTab(v)}
          sx={{ px: 2, borderBottom: '1px solid', borderColor: 'divider' }}
        >
          <Tab
            label={`Pending (${requests.filter((r) => r.status === 'PENDING').length})`}
            id="deact-tab-pending"
          />
          <Tab
            label={`Approved (${requests.filter((r) => r.status === 'APPROVED').length})`}
            id="deact-tab-approved"
          />
          <Tab
            label={`Rejected (${requests.filter((r) => r.status === 'REJECTED').length})`}
            id="deact-tab-rejected"
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
              emptyMessage="No deactivation requests found"
            />
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default AdminDeactivationRequestsPage;
