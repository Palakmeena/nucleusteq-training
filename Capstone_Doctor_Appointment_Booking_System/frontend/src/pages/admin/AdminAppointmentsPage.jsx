import React from 'react';
import { Box, Card, CardContent, Typography } from '@mui/material';
import PageHeader from '../../components/common/PageHeader';
import EmptyState from '../../components/emptyState/EmptyState';
import { CalendarMonth as CalendarIcon } from '@mui/icons-material';

const AdminAppointmentsPage = () => (
  <Box>
    <PageHeader
      title="Appointment Monitoring"
      subtitle="Platform-wide appointment overview"
    />
    <Card>
      <CardContent>
        <EmptyState
          icon={CalendarIcon}
          title="Appointment analytics"
          description="Detailed appointment monitoring will appear here. Use the dashboard for current appointment counts."
        />
      </CardContent>
    </Card>
  </Box>
);

export default AdminAppointmentsPage;