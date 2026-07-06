import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Box, Card, CardContent, Typography, Stack } from '@mui/material';
import { CheckCircle as SuccessIcon } from '@mui/icons-material';
import Button from '../../components/buttons/Button';
import { formatDate, formatTime } from '../../utils/formatters';

const AppointmentSuccessPage = () => {
  const navigate = useNavigate();
  const { appointment, doctor } = useLocation().state || {};

  return (
    <Box maxWidth={560} mx="auto" textAlign="center" py={4}>
      <SuccessIcon sx={{ fontSize: 72, color: 'success.main', mb: 2 }} />
      <Typography variant="h4" fontWeight={800} gutterBottom>
        Appointment Confirmed!
      </Typography>
      <Typography variant="body1" color="text.secondary" mb={4}>
        Your booking has been confirmed. A confirmation has been recorded in your account.
      </Typography>

      {appointment && (
        <Card sx={{ mb: 4, textAlign: 'left' }}>
          <CardContent>
            <Stack spacing={1}>
              <Typography fontWeight={700}>Dr. {doctor?.full_name}</Typography>
              <Typography variant="body2" color="text.secondary">
                {doctor?.specialization}
              </Typography>
              <Typography variant="body2">
                {formatDate(appointment.appointment_date)} at {formatTime(appointment.start_time)}
              </Typography>
              <Typography variant="body2" color="success.main" fontWeight={600}>
                Status: {appointment.status}
              </Typography>
            </Stack>
          </CardContent>
        </Card>
      )}

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} justifyContent="center">
        <Button onClick={() => navigate('/appointments')}>View Appointments</Button>
        <Button variant="outlined" onClick={() => navigate('/dashboard')}>
          Go to Dashboard
        </Button>
      </Stack>
    </Box>
  );
};

export default AppointmentSuccessPage;
