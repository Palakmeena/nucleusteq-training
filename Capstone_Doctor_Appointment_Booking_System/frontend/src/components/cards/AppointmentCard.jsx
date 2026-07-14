import React from 'react';
import { Box, Card, CardContent, Stack, Avatar, Typography, Button } from '@mui/material';
import {
  CalendarMonth as CalendarIcon,
  MedicalServices as DoctorIcon,
  Person as PersonIcon,
} from '@mui/icons-material';
import StatusChip from '../common/StatusChip';
import { formatDate, formatTime } from '../../utils/formatters';

const AppointmentCard = ({
  appointment,
  view = 'patient',
  doctorName,
  patientLabel,
  onCancel,
  onMarkCompleted,
  onMarkNoShow,
  actions,
}) => {
  const isUpcoming =
    appointment.status === 'PENDING' || appointment.status === 'CONFIRMED';

  return (
    <Card sx={{ mb: 2 }}>
      <CardContent>
        <Stack direction="row" spacing={2} alignItems="flex-start">
          <Avatar sx={{ bgcolor: 'primary.light', color: 'primary.main' }}>
            {view === 'doctor' ? <PersonIcon /> : <DoctorIcon />}
          </Avatar>

          <Stack flex={1} spacing={1}>
            <Stack direction="row" justifyContent="space-between" alignItems="flex-start">
              <Box>
                <Typography variant="subtitle1" fontWeight={700}>
                  {view === 'doctor'
                    ? patientLabel || `Patient ${appointment.patient_id?.slice(-6)}`
                    : doctorName || `Doctor ${appointment.doctor_id?.slice(-6)}`}
                </Typography>
                <Stack direction="row" spacing={2} sx={{ mt: 0.5 }}>
                  <Stack direction="row" spacing={0.5} alignItems="center">
                    <CalendarIcon fontSize="small" color="action" />
                    <Typography variant="body2">
                      {formatDate(appointment.appointment_date)}
                    </Typography>
                  </Stack>
                  <Typography variant="body2">
                    {formatTime(appointment.start_time)} – {formatTime(appointment.end_time)}
                  </Typography>
                </Stack>
              </Box>
              <StatusChip status={appointment.status} />
            </Stack>

            {appointment.payment_status && (
              <Typography variant="caption" color="text.secondary">
                Payment: {appointment.payment_status}
              </Typography>
            )}
          </Stack>

          <Stack direction="row" spacing={1}>
            {actions}
            {view === 'patient' && isUpcoming && onCancel && (
              <Button variant="outlined" color="error" size="small" onClick={onCancel}>
                Cancel
              </Button>
            )}
            {view === 'doctor' && appointment.status === 'CONFIRMED' && (
              <>
                {onMarkCompleted && (
                  <Button variant="contained" color="success" size="small" onClick={onMarkCompleted}>
                    Complete
                  </Button>
                )}
                {onMarkNoShow && (
                  <Button variant="outlined" color="warning" size="small" onClick={onMarkNoShow}>
                    No Show
                  </Button>
                )}
              </>
            )}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
};

export default AppointmentCard;