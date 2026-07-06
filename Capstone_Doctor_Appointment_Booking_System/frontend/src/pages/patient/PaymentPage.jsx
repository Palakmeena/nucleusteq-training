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
} from '@mui/material';
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

  return (
    <Box maxWidth={640} mx="auto">
      <PageHeader title="Complete Payment" subtitle="Secure checkout for your appointment" />

      <Card>
        <CardContent>
          <Typography variant="h6" fontWeight={700} gutterBottom>
            Appointment Summary
          </Typography>
          <Stack spacing={1} mb={2}>
            <Typography>Dr. {doctor?.full_name || 'Doctor'}</Typography>
            <Typography variant="body2" color="text.secondary">
              {doctor?.specialization}
            </Typography>
            <Typography variant="body2">
              {formatDate(appointment.appointment_date)} · {formatTime(appointment.start_time)}
            </Typography>
          </Stack>

          <Divider sx={{ my: 2 }} />

          <Typography variant="subtitle1" fontWeight={600} gutterBottom>
            Payment Method
          </Typography>
          <RadioGroup value={method} onChange={(e) => setMethod(e.target.value)}>
            <FormControlLabel value="card" control={<Radio />} label="Credit / Debit Card" />
            <FormControlLabel value="upi" control={<Radio />} label="UPI" />
            <FormControlLabel value="wallet" control={<Radio />} label="Digital Wallet" />
          </RadioGroup>

          <Divider sx={{ my: 2 }} />

          <Stack direction="row" justifyContent="space-between" alignItems="center">
            <Typography variant="h6" fontWeight={700}>
              Total
            </Typography>
            <Typography variant="h5" color="primary.main" fontWeight={800}>
              {formatCurrency(doctor?.consultation_fee)}
            </Typography>
          </Stack>

          <Button fullWidth sx={{ mt: 3 }} loading={paying} onClick={handlePay}>
            Pay Now
          </Button>
        </CardContent>
      </Card>
    </Box>
  );
};

export default PaymentPage;
