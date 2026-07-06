import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import { ErrorOutline as ErrorIcon } from '@mui/icons-material';

const ErrorState = ({ title = 'Something went wrong', message, onRetry }) => (
  <Box sx={{ textAlign: 'center', py: 8, px: 2 }}>
    <ErrorIcon sx={{ fontSize: 56, color: 'error.main', mb: 2 }} />
    <Typography variant="h6" gutterBottom>
      {title}
    </Typography>
    {message && (
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3, maxWidth: 400, mx: 'auto' }}>
        {message}
      </Typography>
    )}
    {onRetry && (
      <Button variant="contained" onClick={onRetry}>
        Try Again
      </Button>
    )}
  </Box>
);

export default ErrorState;
