import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Dialog,
  DialogContent,
  Box,
  Typography,
  Stack,
  Divider,
  RadioGroup,
  FormControlLabel,
  Radio,
  Alert,
  Avatar,
  Grid,
  Paper,
  IconButton,
} from '@mui/material';
import {
  CreditCard as CardIcon,
  AccountBalance as UPIIcon,
  Wallet as WalletIcon,
  Lock as SecureIcon,
  Event as DateIcon,
  AccessTime as TimeIcon,
  Close as CloseIcon,
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import appointmentApi from '../../api/appointmentApi';
import Button from '../buttons/Button';
import { formatCurrency, formatDate, formatTime } from '../../utils/formatters';
import { showError } from '../../utils/errorHandler';

const PaymentModal = ({ open, onClose, appointment, doctor, onSuccess }) => {
  const [method, setMethod] = useState('card');
  const [paying, setPaying] = useState(false);
  const navigate = useNavigate();

  const handlePay = async () => {
    try {
      setPaying(true);
      await appointmentApi.pay(appointment.id);
      toast.success('Payment successful!');
      if (onSuccess) {
        onSuccess();
      } else {
        navigate('/appointments/success', {
          state: { appointment, doctor },
          replace: true,
        });
      }
    } catch (error) {
      showError(error, 'Payment failed');
    } finally {
      setPaying(false);
    }
  };

  if (!appointment) return null;

  const paymentMethods = [
    { value: 'card', label: 'Credit / Debit Card', icon: <CardIcon /> },
    { value: 'upi', label: 'UPI Payment', icon: <UPIIcon /> },
    { value: 'wallet', label: 'Digital Wallet', icon: <WalletIcon /> },
  ];

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogContent sx={{ p: { xs: 2, md: 4 } }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <Typography variant="h5" fontWeight={800}>Complete Payment</Typography>
          <IconButton onClick={onClose} size="small">
            <CloseIcon />
          </IconButton>
        </Box>

        <Alert severity="info" sx={{ mb: 3 }} icon={<SecureIcon />}>
          This is a demo payment. No actual charges will be made.
        </Alert>

        <Grid container spacing={4}>
          <Grid item xs={12} md={5}>
            <Stack spacing={3}>
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

              <Stack direction="row" justifyContent="space-between" alignItems="center">
                <Typography variant="body1" color="text.secondary">
                  Consultation Fee
                </Typography>
                <Typography variant="h5" color="primary.main" fontWeight={800}>
                  {formatCurrency(doctor?.consultation_fee)}
                </Typography>
              </Stack>
            </Stack>
          </Grid>

          <Grid item xs={12} md={7}>
            <Typography variant="h6" fontWeight={700} gutterBottom mb={2}>
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

            <Stack spacing={2}>
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
          </Grid>
        </Grid>
      </DialogContent>
    </Dialog>
  );
};

export default PaymentModal;
