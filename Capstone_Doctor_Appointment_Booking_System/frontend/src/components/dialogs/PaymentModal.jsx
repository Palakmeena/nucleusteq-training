import { useState } from 'react';
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
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
      scroll="paper"
      slotProps={{ paper: { sx: { width: 'calc(100% - 32px)', maxWidth: 920, borderRadius: { xs: 2.5, sm: 3 }, m: 2, overflow: 'hidden', boxShadow: '0 24px 64px rgba(15, 23, 42, 0.24)' } } }}
    >
      <DialogContent sx={{ position: 'relative', p: { xs: 2.5, sm: 3.5, md: 4 } }}>
        <IconButton
          onClick={onClose}
          aria-label="Close payment dialog"
          sx={{ position: 'absolute', top: { xs: 16, sm: 20 }, right: { xs: 16, sm: 20 }, color: 'text.secondary', bgcolor: 'background.default', '&:hover': { bgcolor: 'action.hover', color: 'text.primary' } }}
        >
          <CloseIcon fontSize="small" />
        </IconButton>
        <Box mb={3} pr={5}>
          <Box>
            <Typography variant="h5" fontWeight={800} lineHeight={1.2}>Complete payment</Typography>
            <Typography variant="body2" color="text.secondary" mt={0.75}>Review your appointment and choose a payment method.</Typography>
          </Box>
        </Box>

        <Alert severity="info" sx={{ mb: 3, borderRadius: 2, '& .MuiAlert-message': { py: 0.25 } }} icon={<SecureIcon />}>
          This is a demo payment. No actual charges will be made.
        </Alert>

        <Grid container spacing={{ xs: 2.5, md: 3 }}>
          <Grid size={{ xs: 12, md: 5 }}>
            <Stack spacing={2.5} sx={{ p: { xs: 2.25, sm: 2.5 }, bgcolor: 'background.default', border: '1px solid', borderColor: 'divider', borderRadius: 2.5, height: '100%' }}>
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

              <Stack spacing={1.5}>
                <Typography variant="caption" fontWeight={700} color="text.secondary" letterSpacing={0.7}>
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

              <Stack spacing={0.5} sx={{ p: 2, borderRadius: 2, bgcolor: 'rgba(37, 99, 235, 0.06)' }}>
                <Typography variant="caption" color="text.secondary" fontWeight={700} letterSpacing={0.5}>
                  Consultation Fee
                </Typography>
                <Typography variant="h5" color="primary.main" fontWeight={800} lineHeight={1.15}>
                  {formatCurrency(doctor?.consultation_fee)}
                </Typography>
              </Stack>
            </Stack>
          </Grid>

          <Grid size={{ xs: 12, md: 7 }}>
            <Typography variant="h6" fontWeight={700} mb={0.75}>
              Select Payment Method
            </Typography>
            <Typography variant="body2" color="text.secondary" mb={2.25}>Choose how you would like to pay.</Typography>

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
                      borderRadius: 2.5,
                      cursor: 'pointer',
                      transition: 'all 0.2s',
                      bgcolor: method === pm.value ? 'rgba(37, 99, 235, 0.045)' : 'background.paper',
                      '&:hover': { borderColor: 'primary.main', transform: 'translateY(-1px)', boxShadow: '0 4px 12px rgba(37, 99, 235, 0.10)' },
                    }}
                    onClick={() => setMethod(pm.value)}
                  >
                    <FormControlLabel
                      value={pm.value}
                      control={<Radio size="small" />}
                      label={
                        <Stack direction="row" spacing={1.5} alignItems="center" sx={{ ml: 0.5 }}>
                          <Box sx={{ color: method === pm.value ? 'primary.main' : 'text.secondary', display: 'flex' }}>{pm.icon}</Box>
                          <Typography fontWeight={600}>{pm.label}</Typography>
                        </Stack>
                      }
                      sx={{ m: 0, width: '100%' }}
                    />
                  </Paper>
                ))}
              </Stack>
            </RadioGroup>

            <Divider sx={{ my: 2.5 }} />

            <Stack spacing={1.5} sx={{ p: 2, borderRadius: 2, bgcolor: 'background.default' }}>
              <Stack direction="row" justifyContent="space-between" alignItems="center">
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
              sx={{ mt: 2.5, py: 1.75, borderRadius: 2.5 }}
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
