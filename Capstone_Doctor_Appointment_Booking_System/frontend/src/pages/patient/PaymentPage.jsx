import React, { useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Stack,
  Divider,
  RadioGroup,
  FormControlLabel,
  Radio,
  Alert,
  Chip,
  Avatar,
  Grid,
  Paper,
} from '@mui/material';
import {
  CreditCard as CardIcon,
  AccountBalance as UPIIcon,
  Wallet as WalletIcon,
  Lock as SecureIcon,
  MedicalServices as DoctorIcon,
  Event as DateIcon,
  AccessTime as TimeIcon,
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import appointmentApi from '../../api/appointmentApi';
import PageHeader from '../../components/common/PageHeader';
import Button from '../../components/buttons/Button';
import { formatCurrency, formatDate, formatTime } from '../../utils/formatters';
import { showError } from '../../utils/errorHandler';

const PaymentPage = () => {
  const { appointmentId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { appointment, doctor } = location.state || {};
  const [method, setMethod] = useState('card');
  const [paying, setPaying] = useState(false);

  const handlePay = async () => {
    try {
      setPaying(true);
      await appointmentApi.pay(appointmentId);
      toast.success('Payment successful!');
      navigate('/appointments/success', {
        state: { appointment, doctor },
        replace: true,
      });
    } catch (error) {
      showError(error, 'Payment failed');
    } finally {
      setPaying(false);
    }
  };

  if (!appointment) {
    return (
      <Box>
        <PageHeader title="Payment" subtitle="Appointment details unavailable" />
        <Button onClick={() => navigate('/appointments')}>Go to Appointments</Button>
      </Box>
    );
  }

  const paymentMethods = [
    { value: 'card', label: 'Credit / Debit Card', icon: <CardIcon /> },
    { value: 'upi', label: 'UPI Payment', icon: <UPIIcon /> },
    { value: 'wallet', label: 'Digital Wallet', icon: <WalletIcon /> },
  ];

  return (
    <Box maxWidth={800} mx="auto">
      <PageHeader title="Complete Payment" subtitle="Secure checkout for your appointment" />

      <Alert severity="info" sx={{ mb: 3 }} icon={<SecureIcon />}>
        This is a demo payment. No actual charges will be made.
      </Alert>

      <Grid container spacing={3}>
        {/* Left Column - Doctor Info */}
        <Grid size={{ xs: 12, md: 5 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Stack spacing={3}>
                {/* Doctor Profile */}
                <Stack direction="row" spacing={2} alignItems="center">
                  <Avatar sx={{ width: 64, height: 64, bgcolor: 'primary.main', fontSize: 28 }}>
                    {doctor?.full_name?.charAt(0) || 'D'}
                  </Avatar>
                  <Box>
                    <Typography variant="h6" fontWeight={700}>
                      Dr. {doctor?.full_name || 'Doctor'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {doctor?.specialization}
                    </Typography>
                  </Box>
                </Stack>

                <Divider />

                {/* Appointment Details */}
                <Stack spacing={2}>
                  <Typography variant="subtitle2" fontWeight={600} color="text.secondary">
                    APPOINTMENT DETAILS
                  </Typography>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <DateIcon color="primary" fontSize="small" />
                    <Typography variant="body1">
                      {formatDate(appointment.appointment_date)}
                    </Typography>
                  </Stack>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <TimeIcon color="primary" fontSize="small" />
                    <Typography variant="body1">
                      {formatTime(appointment.start_time)} - {formatTime(appointment.end_time)}
                    </Typography>
                  </Stack>
                </Stack>

                <Divider />

                {/* Fee */}
                <Stack direction="row" justifyContent="space-between" alignItems="center">
                  <Typography variant="body1" color="text.secondary">
                    Consultation Fee
                  </Typography>
                  <Typography variant="h5" color="primary.main" fontWeight={800}>
                    {formatCurrency(doctor?.consultation_fee)}
                  </Typography>
                </Stack>
              </Stack>
            </CardContent>
          </Card>
        </Grid>

        {/* Right Column - Payment */}
        <Grid size={{ xs: 12, md: 7 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={700} gutterBottom mb={3}>
                Select Payment Method
              </Typography>

              <RadioGroup value={method} onChange={(e) => setMethod(e.target.value)}>
                <Stack spacing={2}>
                  {paymentMethods.map((pm) => (
                    <Paper
                      key={pm.value}
                      elevation={method === pm.value ? 3 : 1}
                      sx={{
                        p: 2,
                        border: '2px solid',
                        borderColor: method === pm.value ? 'primary.main' : 'divider',
                        borderRadius: 2,
                        cursor: 'pointer',
                        transition: 'all 0.2s',
                        '&:hover': { borderColor: method === pm.value ? 'primary.main' : 'primary.light' },
                      }}
                      onClick={() => setMethod(pm.value)}
                    >
                      <FormControlLabel
                        value={pm.value}
                        control={<Radio />}
                        label={
                          <Stack direction="row" spacing={2} alignItems="center" sx={{ ml: 1 }}>
                            <Box sx={{ color: 'primary.main' }}>{pm.icon}</Box>
                            <Typography fontWeight={600}>{pm.label}</Typography>
                          </Stack>
                        }
                        sx={{ m: 0, width: '100%' }}
                      />
                    </Paper>
                  ))}
                </Stack>
              </RadioGroup>

              <Divider sx={{ my: 3 }} />

              {/* Order Summary */}
              <Stack spacing={2}>
                <Typography variant="subtitle2" fontWeight={600} color="text.secondary">
                  ORDER SUMMARY
                </Typography>
                <Stack direction="row" justifyContent="space-between">
                  <Typography variant="body1">Consultation Fee</Typography>
                  <Typography variant="body1">{formatCurrency(doctor?.consultation_fee)}</Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography variant="body1">Platform Fee</Typography>
                  <Typography variant="body1" color="success.main">Free</Typography>
                </Stack>
                <Divider />
                <Stack direction="row" justifyContent="space-between" alignItems="center">
                  <Typography variant="h6" fontWeight={700}>
                    Total Amount
                  </Typography>
                  <Typography variant="h5" color="primary.main" fontWeight={800}>
                    {formatCurrency(doctor?.consultation_fee)}
                  </Typography>
                </Stack>
              </Stack>

              <Button
                fullWidth
                size="large"
                sx={{ mt: 3, py: 2.5 }}
                loading={paying}
                onClick={handlePay}
                startIcon={<SecureIcon />}
              >
                {paying ? 'Processing...' : `Pay ${formatCurrency(doctor?.consultation_fee)}`}
              </Button>

              <Stack direction="row" spacing={1} justifyContent="center" mt={2}>
                <SecureIcon fontSize="small" color="action" />
                <Typography variant="caption" color="text.secondary">
                  Secured by 256-bit SSL encryption
                </Typography>
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default PaymentPage;