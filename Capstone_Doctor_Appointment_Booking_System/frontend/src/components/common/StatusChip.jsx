import React from 'react';
import { Chip } from '@mui/material';

const StatusChip = ({ status, sx }) => {
  const getStatusColor = (status) => {
    const statusMap = {
      SCHEDULED: 'info',
      CONFIRMED: 'success',
      COMPLETED: 'success',
      CANCELLED: 'error',
      NO_SHOW: 'warning',
      PENDING: 'warning',
      ACTIVE: 'success',
      INACTIVE: 'error',
      AVAILABLE: 'success',
      BOOKED: 'error',
      // Doctor registration approval statuses
      APPROVED: 'success',
      REJECTED: 'error',
    };
    return statusMap[status] || 'default';
  };

  return (
    <Chip
      label={status}
      color={getStatusColor(status)}
      size="small"
      sx={{
        fontWeight: 600,
        textTransform: 'uppercase',
        fontSize: '0.75rem',
        ...sx,
      }}
    />
  );
};

export default StatusChip;
