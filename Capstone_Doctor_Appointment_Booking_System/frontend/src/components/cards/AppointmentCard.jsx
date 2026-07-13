import {
  Box, Card, CardContent, Stack, Avatar, Typography,
  Button,
} from '@mui/material';
import {
  CalendarMonth as CalendarIcon,
  MedicalServices as DoctorIcon,
  Person as PersonIcon,
  AccessTime as TimeIcon,
  CreditCard as PaymentIcon,
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
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
    <Card
      sx={{
        mb: 2,
        border: '1px solid',
        borderColor: 'divider',
        boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
        transition: 'all 0.2s ease',
        '&:hover': {
          boxShadow: '0 4px 16px rgba(0,0,0,0.10)',
          borderColor: 'primary.light',
          transform: 'translateY(-1px)',
        },
      }}
    >
      <CardContent sx={{ p: 2.5, '&:last-child': { pb: 2.5 } }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems={{ xs: 'stretch', sm: 'flex-start' }}>
          <Avatar
            sx={{
              bgcolor: view === 'doctor' ? 'secondary.light' : 'primary.light',
              color: view === 'doctor' ? 'secondary.main' : 'primary.main',
              width: 48,
              height: 48,
              flexShrink: 0,
            }}
          >
            {view === 'doctor' ? <PersonIcon /> : <DoctorIcon />}
          </Avatar>

          <Stack flex={1} spacing={0.75} minWidth={0}>
            {/* Name + Status row */}
            <Stack direction="row" justifyContent="space-between" alignItems="flex-start" flexWrap="wrap" gap={1}>
              <Typography variant="subtitle1" fontWeight={700} lineHeight={1.3}>
                {view === 'admin' ? (
                  `${patientLabel || appointment.patient_name || 'Patient'} → ${doctorName || (appointment.doctor_name ? `Dr. ${appointment.doctor_name}` : 'Doctor')}`
                ) : view === 'doctor'
                  ? appointment.patient_name || patientLabel || `Patient ${appointment.patient_id?.slice(-6)}`
                  : (
                    <Link
                      to={`/doctor/${appointment.doctor_id}`}
                      style={{ textDecoration: 'none', color: 'inherit' }}
                      className="doctor-profile-link"
                    >
                      {appointment.doctor_name ? `Dr. ${appointment.doctor_name}` : doctorName || `Doctor ${appointment.doctor_id?.slice(-6)}`}
                    </Link>
                  )}
              </Typography>
              <StatusChip status={appointment.status} />
            </Stack>

            {/* Date & Time */}
            <Stack direction="row" spacing={2} flexWrap="wrap">
              <Stack direction="row" spacing={0.5} alignItems="center">
                <CalendarIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
                <Typography variant="body2" color="text.secondary">
                  {formatDate(appointment.appointment_date)}
                </Typography>
              </Stack>
              <Stack direction="row" spacing={0.5} alignItems="center">
                <TimeIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
                <Typography variant="body2" color="text.secondary">
                  {formatTime(appointment.start_time)} – {formatTime(appointment.end_time)}
                </Typography>
              </Stack>
            </Stack>

            {/* Payment status badge */}
            {appointment.payment_status && (
              <Stack direction="row" spacing={0.5} alignItems="center">
                <PaymentIcon sx={{ fontSize: 13, color: 'text.disabled' }} />
                <Typography variant="caption" color="text.disabled" fontWeight={600}>
                  Payment:{' '}
                  <Box
                    component="span"
                    sx={{
                      color: appointment.payment_status === 'PAID' ? 'success.main' : 'warning.main',
                      fontWeight: 700,
                    }}
                  >
                    {appointment.payment_status}
                  </Box>
                </Typography>
              </Stack>
            )}
          </Stack>

          {/* Action buttons */}
          <Stack
            direction="row"
            spacing={1}
            alignItems="center"
            flexShrink={0}
            flexWrap="wrap"
            useFlexGap
            sx={{ width: { xs: '100%', sm: 'auto' }, justifyContent: { xs: 'flex-end', sm: 'initial' } }}
          >
            {actions}
            {isUpcoming && onCancel && (
              <Button
                variant="outlined"
                size="small"
                onClick={onCancel}
                sx={{
                  color: 'error.main',
                  border: '1px solid',
                  borderColor: 'rgba(220, 38, 38, 0.25)',
                  fontWeight: 700,
                  fontSize: '0.75rem',
                  px: 2,
                  py: 0.75,
                  borderRadius: 2,
                  boxShadow: 'none',
                  textTransform: 'none',
                  '&:hover': { bgcolor: 'error.light', borderColor: 'error.main' },
                  transition: 'all 0.2s',
                }}
              >
                Cancel
              </Button>
            )}
            {view === 'doctor' && appointment.status === 'CONFIRMED' && (
              <>
                {onMarkCompleted && (
                  <Button
                    variant="contained"
                    color="success"
                    size="small"
                    onClick={onMarkCompleted}
                    sx={{
                      fontWeight: 700,
                      textTransform: 'none',
                      borderRadius: 2,
                      boxShadow: 'none',
                      '&:hover': { boxShadow: '0 2px 8px rgba(22,163,74,0.35)' },
                    }}
                  >
                    Complete
                  </Button>
                )}
                {onMarkNoShow && (
                  <Button
                    variant="outlined"
                    color="warning"
                    size="small"
                    onClick={onMarkNoShow}
                    sx={{
                      fontWeight: 700,
                      textTransform: 'none',
                      borderRadius: 2,
                    }}
                  >
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
