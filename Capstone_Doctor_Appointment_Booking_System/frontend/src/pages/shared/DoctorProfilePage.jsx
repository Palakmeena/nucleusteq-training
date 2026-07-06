import React, { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Chip,
  Avatar,
  Stack,
  Divider,
  Tabs,
  Tab,
  Paper,
} from '@mui/material';
import {
  LocationOn as LocationIcon,
  School as SchoolIcon,
  Work as WorkIcon,
  ArrowBack as ArrowBackIcon,
  Verified as VerifiedIcon,
} from '@mui/icons-material';
import dayjs from 'dayjs';
import { toast } from 'react-toastify';
import doctorApi from '../../api/doctorApi';
import slotApi from '../../api/slotApi';
import appointmentApi from '../../api/appointmentApi';
import { useAuth } from '../../context/AuthContext';
import TimeSlotCard from '../../components/cards/TimeSlotCard';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import EmptyState from '../../components/emptyState/EmptyState';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { showError } from '../../utils/errorHandler';

const DoctorProfilePage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [doctor, setDoctor] = useState(null);
  const [slots, setSlots] = useState([]);
  const [selectedDate, setSelectedDate] = useState(dayjs().format('YYYY-MM-DD'));
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [loading, setLoading] = useState(true);
  const [booking, setBooking] = useState(false);
  const [tab, setTab] = useState(0);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const res = await doctorApi.getDoctorById(id);
        setDoctor(res.data);
      } catch (error) {
        showError(error, 'Failed to load doctor profile');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  useEffect(() => {
    const loadSlots = async () => {
      try {
        const res = await slotApi.getSlotsByDoctor(id);
        setSlots(res.data || []);
        setSelectedSlot(null);
      } catch {
        setSlots([]);
      }
    };
    if (id) loadSlots();
  }, [id, selectedDate]);

  const slotsForDate = useMemo(
    () => slots.filter((s) => s.date === selectedDate && !s.is_booked),
    [slots, selectedDate]
  );

  const handleBook = async () => {
    if (!isAuthenticated) {
      toast.info('Please sign in to book an appointment');
      navigate('/login', { state: { from: `/doctors/${id}` } });
      return;
    }
    if (!selectedSlot) {
      toast.error('Please select a time slot');
      return;
    }

    try {
      setBooking(true);
      const res = await appointmentApi.book({
        doctor_id: id,
        slot_id: selectedSlot.id,
        appointment_date: selectedDate,
      });
      toast.success('Appointment reserved! Complete payment to confirm.');
      navigate(`/appointments/${res.data.id}/payment`, {
        state: { appointment: res.data, doctor },
      });
    } catch (error) {
      showError(error, 'Failed to book appointment');
    } finally {
      setBooking(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading doctor profile..." />;
  if (!doctor) {
    return (
      <EmptyState
        title="Doctor not found"
        description="This profile may have been removed"
        action
        actionText="Go Back"
        onAction={() => navigate(-1)}
      />
    );
  }

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)} sx={{ mb: 2 }}>
        Back
      </Button>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Grid container spacing={3} alignItems="center">
            <Grid size={{ xs: 12, md: 3 }}>
              <Box position="relative" display="inline-block">
                <Avatar sx={{ width: 120, height: 120, bgcolor: 'primary.main', fontSize: 48 }}>
                  {doctor.full_name?.charAt(0)}
                </Avatar>
                {doctor.is_active && (
                  <Chip
                    icon={<VerifiedIcon />}
                    label="Verified"
                    color="success"
                    size="small"
                    sx={{ position: 'absolute', bottom: -4, right: -4 }}
                  />
                )}
              </Box>
            </Grid>
            <Grid size={{ xs: 12, md: 9 }}>
              <Typography variant="h4" fontWeight={800}>
                Dr. {doctor.full_name}
              </Typography>
              <Typography variant="h6" color="primary.main" fontWeight={600}>
                {doctor.specialization}
              </Typography>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} mt={2}>
                <Stack direction="row" spacing={0.5} alignItems="center">
                  <LocationIcon fontSize="small" color="action" />
                  <Typography variant="body2">{doctor.clinic_address}</Typography>
                </Stack>
                <Stack direction="row" spacing={0.5} alignItems="center">
                  <WorkIcon fontSize="small" color="action" />
                  <Typography variant="body2">{doctor.experience} years</Typography>
                </Stack>
                <Stack direction="row" spacing={0.5} alignItems="center">
                  <SchoolIcon fontSize="small" color="action" />
                  <Typography variant="body2">{doctor.qualification}</Typography>
                </Stack>
              </Stack>
              <Typography variant="h5" color="primary.main" fontWeight={700} mt={2}>
                {formatCurrency(doctor.consultation_fee)} per visit
              </Typography>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      <Paper sx={{ mb: 3 }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)}>
          <Tab label="Book Appointment" />
          <Tab label="About" />
        </Tabs>
      </Paper>

      {tab === 0 && (
        <Grid container spacing={3}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card>
              <CardContent>
                <Typography variant="h6" fontWeight={700} gutterBottom>
                  Select Date
                </Typography>
                <input
                  type="date"
                  value={selectedDate}
                  min={dayjs().format('YYYY-MM-DD')}
                  onChange={(e) => setSelectedDate(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '12px',
                    borderRadius: '10px',
                    border: '1px solid #d0d7e2',
                    fontSize: '1rem',
                  }}
                />
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 8 }}>
            <Card>
              <CardContent>
                <Typography variant="h6" fontWeight={700} gutterBottom>
                  Available Slots — {formatDate(selectedDate)}
                </Typography>
                {slotsForDate.length > 0 ? (
                  <Grid container spacing={2}>
                    {slotsForDate.map((slot) => (
                      <Grid size={{ xs: 6, sm: 4, md: 3 }} key={slot.id}>
                        <TimeSlotCard
                          slot={slot}
                          selected={selectedSlot?.id === slot.id}
                          onSelect={setSelectedSlot}
                        />
                      </Grid>
                    ))}
                  </Grid>
                ) : (
                  <EmptyState
                    title="No slots available"
                    description="Try another date or check back later"
                  />
                )}
                {selectedSlot && (
                  <Box textAlign="center" mt={3}>
                    <Button
                      variant="contained"
                      size="large"
                      onClick={handleBook}
                      disabled={booking}
                    >
                      {booking ? 'Booking...' : `Book — ${formatCurrency(doctor.consultation_fee)}`}
                    </Button>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {tab === 1 && (
        <Card>
          <CardContent>
            <Typography variant="h6" fontWeight={700} gutterBottom>
              About Dr. {doctor.full_name}
            </Typography>
            <Typography variant="body1" color="text.secondary" paragraph>
              Dr. {doctor.full_name} is a {doctor.specialization?.toLowerCase()} with{' '}
              {doctor.experience} years of clinical experience, practicing at{' '}
              {doctor.clinic_address}.
            </Typography>
            <Divider sx={{ my: 2 }} />
            <Typography variant="subtitle2" color="text.secondary">
              License: {doctor.license_number}
            </Typography>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default DoctorProfilePage;
