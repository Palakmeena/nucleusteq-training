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

      <Alert
        severity="info"
        sx={{
          mb: 3,
          borderRadius: 2,
          '& .MuiAlert-message': { display: 'flex', alignItems: 'center', gap: 1 },
        }}
        icon={<SecureIcon />}
      >
        This is a demo payment. No actual charges will be made.
      </Alert>

      <Grid container spacing={3}>
        {/* Left Column - Doctor Info */}
        <Grid size={{ xs: 12, md: 5 }}>
          <Card
            sx={{
              height: '100%',
              border: '1px solid',
              borderColor: 'divider',
              boxShadow: '0 2px 12px rgba(0,0,0,0.06)',
            }}
          >
            <CardContent sx={{ p: 3 }}>
              <Stack spacing={3}>
                {/* Doctor Profile */}
                <Stack direction="row" spacing={2} alignItems="center">
                  <Avatar
                    sx={{
                      width: 64,
                      height: 64,
                      bgcolor: 'primary.main',
                      fontSize: 26,
                      fontWeight: 800,
                    }}
                  >
                    {doctor?.full_name?.charAt(0) || 'D'}
                  </Avatar>
                  <Box>
                    <Typography variant="h6" fontWeight={700} lineHeight={1.3}>
                      Dr. {doctor?.full_name || 'Doctor'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" mt={0.25}>
                      {doctor?.specialization}
                    </Typography>
                  </Box>
                </Stack>

                <Divider />

                {/* Appointment Details */}
                <Stack spacing={1.5}>
                  <Typography
                    variant="caption"
                    fontWeight={700}
                    color="text.secondary"
                    letterSpacing={0.8}
                    textTransform="uppercase"
                  >
                    Appointment Details
                  </Typography>
                  <Stack direction="row" spacing={1.5} alignItems="center">
                    <Box
                      sx={{
                        width: 32,
                        height: 32,
                        borderRadius: 1.5,
                        bgcolor: 'primary.light',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <DateIcon sx={{ fontSize: 16, color: 'primary.main' }} />
                    </Box>
                    <Typography variant="body2" fontWeight={500}>
                      {formatDate(appointment.appointment_date)}
                    </Typography>
                  </Stack>
                  <Stack direction="row" spacing={1.5} alignItems="center">
                    <Box
                      sx={{
                        width: 32,
                        height: 32,
                        borderRadius: 1.5,
                        bgcolor: 'primary.light',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <TimeIcon sx={{ fontSize: 16, color: 'primary.main' }} />
                    </Box>
                    <Typography variant="body2" fontWeight={500}>
                      {formatTime(appointment.start_time)} – {formatTime(appointment.end_time)}
                    </Typography>
                  </Stack>
                </Stack>

                <Divider />

                {/* Fee row - fixed spacing between label and amount */}
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    bgcolor: 'primary.50',
                    borderRadius: 2,
                    px: 2,
                    py: 1.5,
                    border: '1px solid',
                    borderColor: 'primary.100',
                    background: 'linear-gradient(135deg, rgba(37,99,235,0.06) 0%, rgba(59,130,246,0.04) 100%)',
                  }}
                >
                  <Typography variant="body2" color="text.secondary" fontWeight={600}>
                    Consultation Fee
                  </Typography>
                  <Typography variant="h6" color="primary.main" fontWeight={800}>
                    {formatCurrency(doctor?.consultation_fee)}
                  </Typography>
                </Box>
              </Stack>
            </CardContent>
          </Card>
        </Grid>

        {/* Right Column - Payment */}
        <Grid size={{ xs: 12, md: 7 }}>
          <Card
            sx={{
              border: '1px solid',
              borderColor: 'divider',
              boxShadow: '0 2px 12px rgba(0,0,0,0.06)',
            }}
          >
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" fontWeight={700} mb={2.5}>
                Select Payment Method
              </Typography>

              <RadioGroup value={method} onChange={(e) => setMethod(e.target.value)}>
                <Stack spacing={1.5}>
                  {paymentMethods.map((pm) => (
                    <Paper
                      key={pm.value}
                      elevation={0}
                      sx={{
                        p: 1.75,
                        border: '2px solid',
                        borderColor: method === pm.value ? 'primary.main' : 'divider',
                        borderRadius: 2,
                        cursor: 'pointer',
                        transition: 'all 0.2s',
                        bgcolor: method === pm.value ? 'rgba(37,99,235,0.04)' : 'transparent',
                        '&:hover': {
                          borderColor: method === pm.value ? 'primary.main' : 'primary.light',
                          bgcolor: method === pm.value ? 'rgba(37,99,235,0.04)' : 'rgba(0,0,0,0.02)',
                        },
                      }}
                      onClick={() => setMethod(pm.value)}
                    >
                      <FormControlLabel
                        value={pm.value}
                        control={<Radio size="small" />}
                        label={
                          <Stack direction="row" spacing={1.5} alignItems="center" sx={{ ml: 0.5 }}>
                            <Box sx={{ color: method === pm.value ? 'primary.main' : 'text.secondary' }}>
                              {pm.icon}
                            </Box>
                            <Typography fontWeight={600} fontSize="0.9rem">
                              {pm.label}
                            </Typography>
                          </Stack>
                        }
                        sx={{ m: 0, width: '100%' }}
                      />
                    </Paper>
                  ))}
                </Stack>
              </RadioGroup>

              <Divider sx={{ my: 2.5 }} />

              {/* Order Summary */}
              <Stack spacing={1.5}>
                <Typography
                  variant="caption"
                  fontWeight={700}
                  color="text.secondary"
                  letterSpacing={0.8}
                  textTransform="uppercase"
                >
                  Order Summary
                </Typography>
                <Stack direction="row" justifyContent="space-between" alignItems="center">
                  <Typography variant="body2" color="text.secondary">Consultation Fee</Typography>
                  <Typography variant="body2" fontWeight={600}>
                    {formatCurrency(doctor?.consultation_fee)}
                  </Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between" alignItems="center">
                  <Typography variant="body2" color="text.secondary">Platform Fee</Typography>
                  <Chip label="Free" size="small" color="success" sx={{ fontWeight: 700, height: 22 }} />
                </Stack>
                <Divider />
                {/* Total row - fixed to have proper gap between label and amount */}
                <Stack
                  direction="row"
                  justifyContent="space-between"
                  alignItems="center"
                  sx={{
                    bgcolor: 'rgba(37,99,235,0.06)',
                    borderRadius: 2,
                    px: 2,
                    py: 1.25,
                  }}
                >
                  <Typography variant="body1" fontWeight={700}>
                    Total Amount
                  </Typography>
                  <Typography variant="h6" color="primary.main" fontWeight={800}>
                    {formatCurrency(doctor?.consultation_fee)}
                  </Typography>
                </Stack>
              </Stack>

              <Button
                fullWidth
                size="large"
                sx={{ mt: 3, py: 1.75, borderRadius: 2, fontWeight: 700, fontSize: '1rem' }}
                loading={paying}
                onClick={handlePay}
                startIcon={<SecureIcon />}
              >
                {paying ? 'Processing...' : `Pay ${formatCurrency(doctor?.consultation_fee)}`}
              </Button>

              <Stack direction="row" spacing={1} justifyContent="center" mt={2} alignItems="center">
                <SecureIcon fontSize="small" sx={{ color: 'text.disabled', fontSize: 14 }} />
                <Typography variant="caption" color="text.disabled">
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
