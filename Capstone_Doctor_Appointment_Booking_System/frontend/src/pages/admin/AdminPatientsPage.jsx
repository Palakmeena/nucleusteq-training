import React, { useEffect, useState } from 'react';
import { Box, Card, CardContent, Typography } from '@mui/material';
import PageHeader from '../../components/common/PageHeader';
import DataTable from '../../components/tables/DataTable';
import StatusChip from '../../components/common/StatusChip';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import adminApi from '../../api/adminApi';
import { showError } from '../../utils/errorHandler';
import { formatDate } from '../../utils/formatters';

const AdminPatientsPage = () => {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const res = await adminApi.getUsers();
        setPatients((res.data || []).filter((u) => u.role === 'PATIENT'));
      } catch (error) {
        showError(error, 'Failed to load patients');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const columns = [
    { id: 'full_name', label: 'Name' },
    { id: 'email', label: 'Email' },
    { id: 'phone', label: 'Phone' },
    {
      id: 'status',
      label: 'Status',
      render: (_, row) => <StatusChip status={row.is_active ? 'ACTIVE' : 'INACTIVE'} />,
    },
    {
      id: 'created_at',
      label: 'Joined',
      render: (_, row) => formatDate(row.created_at),
    },
  ];

  return (
    <Box>
      <PageHeader title="Patients" subtitle="View registered patient accounts" />

      {loading ? (
        <LoadingSpinner />
      ) : (
        <Card>
          <CardContent>
            <DataTable columns={columns} data={patients} emptyMessage="No patients found" />
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default AdminPatientsPage;
