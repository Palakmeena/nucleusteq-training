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
} from '@mui/material';
import {
  LocationOn as LocationIcon,
  School as SchoolIcon,
  Work as WorkIcon,
  ArrowBack as ArrowBackIcon,
  Verified as VerifiedIcon,
  AttachMoney as FeeIcon,
  Badge as LicenseIcon,
  CalendarMonth as CalendarIcon,
  EventAvailable as SlotIcon,
} from '@mui/icons-material';
import dayjs from 'dayjs';
import { toast } from 'react-toastify';
import doctorApi from '../../api/doctorApi';
import slotApi from '../../api/slotApi';
import appointmentApi from '../../api/appointmentApi';
import { useAuth } from '../../context/AuthContext';
import PaymentModal from '../../components/dialogs/PaymentModal';
import TimeSlotCard from '../../components/cards/TimeSlotCard';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import EmptyState from '../../components/emptyState/EmptyState';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { showError } from '../../utils/errorHandler';

/* ── Small info row ────────────────────────────────────────────────── */
const InfoRow = ({ icon, label, value }) => (
  <Stack direction="row" spacing={1.5} alignItems="flex-start">
    <Box
      sx={{
        width: 32,
        height: 32,
        borderRadius: 1.5,
        bgcolor: '#eff6ff',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        flexShrink: 0,
      }}
    >
      {React.cloneElement(icon, { sx: { fontSize: 16, color: '#2563eb' } })}
    </Box>
    <Box>
      <Typography variant="caption" color="text.secondary" fontWeight={600}
        sx={{ textTransform: 'uppercase', letterSpacing: 0.4, display: 'block' }}>
        {label}
      </Typography>
      <Typography variant="body2" fontWeight={500}>
        {value || '—'}
      </Typography>
    </Box>
  </Stack>
);

/* ── Slot date group heading ────────────────────────────────────────── */
const DateSection = ({ date, slots, selectedSlot, onSelect }) => (
  <Box>
    <Stack direction="row" spacing={1} alignItems="center" mb={1.5}>
      <CalendarIcon sx={{ fontSize: 16, color: 'primary.main' }} />
      <Typography variant="subtitle2" fontWeight={700} color="primary.main">
        {formatDate(date)}
      </Typography>
      <Chip
        label={`${slots.filter(s => !s.is_booked).length} available`}
        size="small"
        sx={{ bgcolor: '#f0fdf4', color: '#15803d', fontWeight: 600, fontSize: '0.65rem', height: 18 }}
      />
    </Stack>
    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
      {slots.map((slot) => (
        <TimeSlotCard
          key={slot.id}
          slot={slot}
          selected={selectedSlot?.id === slot.id}
          onSelect={onSelect}
          disabled={slot.is_booked}
        />
      ))}
    </Box>
  </Box>
);

/* ── Main page component ─────────────────────────────────────────────── */
const DoctorProfilePage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();

  const [doctor, setDoctor] = useState(null);
  const [slots, setSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);   // null until slots load
  const [loading, setLoading] = useState(true);
  const [booking, setBooking] = useState(false);
  const [paymentAppointment, setPaymentAppointment] = useState(null);

  /* ── fetch doctor profile ── */
  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const res = await doctorApi.getDoctorById(id);
        setDoctor(res.data);
      } catch (err) {
        showError(err, 'Failed to load doctor profile');
      } finally {
        setLoading(false);
      }
    };
    if (id) load();
  }, [id]);

  /* ── fetch all slots for this doctor, pick earliest available date ── */
  useEffect(() => {
    const loadSlots = async () => {
      try {
        const res = await slotApi.getSlotsByDoctor(id);
        const all = res.data || [];
        setSlots(all);
        setSelectedSlot(null);

        const today = dayjs().format('YYYY-MM-DD');
        const futureDates = [...new Set(all.map(s => s.date))]
          .filter(d => d >= today)
          .sort();
        if (futureDates.length > 0) setSelectedDate(futureDates[0]);
      } catch {
        setSlots([]);
      }
    };
    if (id) loadSlots();
  }, [id]);

  /* ── group future slots by date, sorted ── */
  const slotsByDate = useMemo(() => {
    const today = dayjs().format('YYYY-MM-DD');
    const future = slots.filter(s => s.date >= today);
    const grouped = {};
    future.forEach(s => {
      if (!grouped[s.date]) grouped[s.date] = [];
      grouped[s.date].push(s);
    });
    // sort each date's slots by start time
    Object.values(grouped).forEach(arr =>
      arr.sort((a, b) => a.start_time.localeCompare(b.start_time))
    );
    return Object.fromEntries(Object.entries(grouped).sort(([a], [b]) => a.localeCompare(b)));
  }, [slots]);

  const availableDates = Object.keys(slotsByDate);

  /* ── book ── */
  const handleBook = async () => {
    if (!isAuthenticated) {
      toast.info('Please sign in to book an appointment');
      navigate('/login', { state: { from: `/doctor/${id}` } });
      return;
    }
    if (!selectedSlot) {
      toast.error('Please select a time slot first');
      return;
    }
    try {
      setBooking(true);
      const res = await appointmentApi.book({
        doctor_id: id,
        slot_id: selectedSlot.id,
        appointment_date: selectedSlot.date,
      });
      toast.success('Appointment reserved! Complete payment to confirm.');
      setPaymentAppointment(res.data);
    } catch (err) {
      showError(err, 'Failed to book appointment');
    } finally {
      setBooking(false);
    }
  };

  /* ── guards ── */
  if (loading) return <LoadingSpinner message="Loading doctor profile…" />;
  if (!doctor) {
    return (
      <EmptyState
        title="Doctor not found"
        description="This profile may have been removed"
        action actionText="Go Back" onAction={() => navigate(-1)}
      />
    );
  }

  return (
    <Box maxWidth={960} mx="auto">
      {/* Back link */}
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate(-1)}
        sx={{ mb: 2.5, color: 'text.secondary' }}
      >
        Find Doctors
      </Button>

      {/* ══════ HERO HEADER ══════ */}
      <Card
        sx={{
          mb: 3,
          background: 'linear-gradient(135deg, #1e40af 0%, #2563eb 60%, #3b82f6 100%)',
          color: '#fff',
          overflow: 'hidden',
          position: 'relative',
        }}
      >
        {/* decorative blobs */}
        <Box sx={{ position: 'absolute', width: 260, height: 260, borderRadius: '50%',
          bgcolor: 'rgba(255,255,255,0.05)', top: -60, right: -40 }} />
        <Box sx={{ position: 'absolute', width: 160, height: 160, borderRadius: '50%',
          bgcolor: 'rgba(255,255,255,0.06)', bottom: -30, right: 140 }} />

        <CardContent sx={{ p: { xs: 3, md: 4 }, position: 'relative', zIndex: 1 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={3} alignItems={{ xs: 'center', sm: 'flex-start' }}>
            {/* Avatar */}
            <Avatar sx={{
              width: 100, height: 100, fontSize: 38, fontWeight: 800,
              bgcolor: 'rgba(255,255,255,0.18)', border: '3px solid rgba(255,255,255,0.35)',
              color: '#fff', flexShrink: 0,
            }}>
              {doctor.full_name?.charAt(0)}
            </Avatar>

            {/* Meta */}
            <Box flex={1} textAlign={{ xs: 'center', sm: 'left' }}>
              <Stack direction="row" spacing={1} flexWrap="wrap" justifyContent={{ xs: 'center', sm: 'flex-start' }} mb={0.5}>
                <Typography variant="h4" fontWeight={800} color="inherit">
                  Dr. {doctor.full_name}
                </Typography>
                {doctor.is_active && (
                  <Chip
                    icon={<VerifiedIcon />}
                    label="Verified"
                    size="small"
                    sx={{ bgcolor: 'rgba(22,163,74,0.3)', color: '#fff',
                      border: '1px solid rgba(22,163,74,0.5)', fontWeight: 700 }}
                  />
                )}
              </Stack>

              <Typography variant="h6" fontWeight={600}
                sx={{ color: 'rgba(255,255,255,0.85)', mb: 1.5 }}>
                {doctor.specialization}
              </Typography>

              {/* Key stats in a row */}
              <Stack
                direction="row" spacing={0} flexWrap="wrap"
                divider={<Box sx={{ mx: 1.5, opacity: 0.3 }}>·</Box>}
                justifyContent={{ xs: 'center', sm: 'flex-start' }}
              >
                <Stack direction="row" spacing={0.5} alignItems="center">
                  <WorkIcon sx={{ fontSize: 14, opacity: 0.8 }} />
                  <Typography variant="body2" sx={{ opacity: 0.9 }}>
                    {doctor.experience} yrs experience
                  </Typography>
                </Stack>
                <Stack direction="row" spacing={0.5} alignItems="center">
                  <LocationIcon sx={{ fontSize: 14, opacity: 0.8 }} />
                  <Typography variant="body2" sx={{ opacity: 0.9 }}>
                    {doctor.clinic_address}
                  </Typography>
                </Stack>
              </Stack>
            </Box>

            {/* Fee badge */}
            <Box
              sx={{
                bgcolor: 'rgba(255,255,255,0.15)', borderRadius: 2,
                border: '1px solid rgba(255,255,255,0.25)',
                px: 2.5, py: 1.5, textAlign: 'center', flexShrink: 0,
              }}
            >
              <Typography variant="caption" sx={{ opacity: 0.8, display: 'block' }}>
                Consultation Fee
              </Typography>
              <Typography variant="h5" fontWeight={800} color="inherit">
                {formatCurrency(doctor.consultation_fee)}
              </Typography>
            </Box>
          </Stack>
        </CardContent>
      </Card>

      {/* ══════ TWO-COLUMN LAYOUT ══════ */}
      <Grid container spacing={3} alignItems="flex-start">

        {/* ── LEFT: Doctor details ── */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              <Stack direction="row" spacing={1} alignItems="center" mb={2.5}>
                <SchoolIcon color="primary" fontSize="small" />
                <Typography variant="h6" fontWeight={700}>About</Typography>
              </Stack>
              <Stack spacing={2.5} divider={<Divider />}>
                <InfoRow icon={<SchoolIcon />}  label="Qualification"    value={doctor.qualification} />
                <InfoRow icon={<WorkIcon />}    label="Experience"       value={`${doctor.experience} years`} />
                <InfoRow icon={<LocationIcon />} label="Clinic Address"  value={doctor.clinic_address} />
                <InfoRow icon={<FeeIcon />}     label="Consultation Fee" value={formatCurrency(doctor.consultation_fee)} />
                {user?.role !== 'PATIENT' && (
                  <InfoRow icon={<LicenseIcon />} label="License No."      value={doctor.license_number} />
                )}
              </Stack>
            </CardContent>
          </Card>
        </Grid>

        {/* ── RIGHT: Slot booking ── */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent sx={{ p: 3 }}>
              {/* Section header */}
              <Stack direction="row" spacing={1} alignItems="center" mb={3}>
                <SlotIcon color="primary" fontSize="small" />
                <Typography variant="h6" fontWeight={700}>Book an Appointment</Typography>
              </Stack>

              {availableDates.length === 0 ? (
                /* No slots at all */
                <EmptyState
                  title="No availability yet"
                  description="This doctor hasn't added any slots. Check back soon."
                />
              ) : (
                <>
                  {/* Date tabs (clickable chips) */}
                  <Box mb={3}>
                    <Typography variant="caption" color="text.secondary" fontWeight={600}
                      sx={{ textTransform: 'uppercase', letterSpacing: 0.5, display: 'block', mb: 1 }}>
                      Select a Date
                    </Typography>
                    <Stack direction="row" flexWrap="wrap" gap={1}>
                      {availableDates.map(d => (
                        <Chip
                          key={d}
                          label={
                            <Stack alignItems="center" spacing={0}>
                              <Typography variant="caption" sx={{ lineHeight: 1.2, fontWeight: 700, fontSize: '0.75rem' }}>
                                {dayjs(d).format('MMM D')}
                              </Typography>
                              <Typography variant="caption" sx={{ lineHeight: 1, fontSize: '0.6rem', opacity: 0.8 }}>
                                {dayjs(d).format('ddd')}
                              </Typography>
                            </Stack>
                          }
                          onClick={() => {
                            setSelectedDate(d);
                            setSelectedSlot(null);
                          }}
                          sx={{
                            height: 48,
                            px: 1,
                            borderRadius: 2,
                            cursor: 'pointer',
                            border: '1.5px solid',
                            borderColor: d === selectedDate ? 'primary.main' : 'divider',
                            bgcolor: d === selectedDate ? 'primary.main' : 'background.paper',
                            color: d === selectedDate ? '#fff' : 'text.primary',
                            '&:hover': {
                              bgcolor: d === selectedDate ? 'primary.dark' : '#f1f5f9',
                              borderColor: 'primary.main',
                            },
                            '& .MuiChip-label': { px: 1.5 },
                          }}
                        />
                      ))}
                    </Stack>
                  </Box>

                  <Divider sx={{ mb: 3 }} />

                  {/* Slots for selected date */}
                  {selectedDate && slotsByDate[selectedDate] ? (
                    <DateSection
                      date={selectedDate}
                      slots={slotsByDate[selectedDate]}
                      selectedSlot={selectedSlot}
                      onSelect={setSelectedSlot}
                    />
                  ) : (
                    <Typography variant="body2" color="text.secondary">
                      Select a date above to see available time slots.
                    </Typography>
                  )}

                  {/* Book CTA */}
                  {selectedSlot && (
                    <Box
                      mt={3} pt={3}
                      borderTop="1px solid"
                      borderColor="divider"
                    >
                      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between">
                        <Box>
                          <Typography variant="body2" color="text.secondary">Selected slot</Typography>
                          <Typography variant="subtitle1" fontWeight={700}>
                            {formatDate(selectedSlot.date)} · {selectedSlot.start_time} – {selectedSlot.end_time}
                          </Typography>
                        </Box>
                        <Button
                          variant="contained"
                          size="large"
                          onClick={handleBook}
                          disabled={booking}
                          sx={{ px: 4, flexShrink: 0 }}
                        >
                          {booking ? 'Booking…' : `Confirm — ${formatCurrency(doctor.consultation_fee)}`}
                        </Button>
                      </Stack>
                    </Box>
                  )}
                </>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Payment modal */}
      <PaymentModal
        open={!!paymentAppointment}
        onClose={() => setPaymentAppointment(null)}
        appointment={paymentAppointment}
        doctor={doctor}
        onSuccess={() => {
          setPaymentAppointment(null);
          navigate('/appointment-success', {
            state: { appointment: paymentAppointment, doctor },
            replace: true,
          });
        }}
      />
    </Box>
  );
};

export default DoctorProfilePage;
