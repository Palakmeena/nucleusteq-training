import React, { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import {
  Box,
  Card,
  CardContent,
  Stack,
  MenuItem,
  Grid,
  Typography,
  Chip,
  Divider,
  Avatar,
  TextField,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  Person as PersonIcon,
  School as SchoolIcon,
  Work as WorkIcon,
  LocalHospital as HospitalIcon,
  Phone as PhoneIcon,
  Email as EmailIcon,
  Badge as BadgeIcon,
  AttachMoney as FeeIcon,
  LocationOn as LocationIcon,
  Edit as EditIcon,
  Save as SaveIcon,
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import Button from '../../components/buttons/Button';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import doctorApi from '../../api/doctorApi';
import { SPECIALIZATIONS } from '../../constants/specializations';
import { showError } from '../../utils/errorHandler';
import { formatCurrency } from '../../utils/formatters';

/* ─── Small info row used in the read-only view ─── */
const InfoRow = ({ icon, label, value }) => (
  <Stack direction="row" spacing={1.5} alignItems="flex-start">
    <Box
      sx={{
        width: 36,
        height: 36,
        borderRadius: 1.5,
        bgcolor: 'primary.light',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        flexShrink: 0,
        mt: 0.25,
      }}
    >
      {React.cloneElement(icon, { sx: { fontSize: 18, color: 'primary.main' } })}
    </Box>
    <Stack>
      <Typography variant="caption" color="text.secondary" fontWeight={600} textTransform="uppercase" letterSpacing={0.5}>
        {label}
      </Typography>
      <Typography variant="body2" fontWeight={500} color="text.primary">
        {value || '—'}
      </Typography>
    </Stack>
  </Stack>
);

const DoctorProfilePage = () => {
  // ── state ──────────────────────────────────────────────────────────────
  const [profile, setProfile] = useState(null);   // raw data from API
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [fetchError, setFetchError] = useState(null);

  const { register, handleSubmit, reset, control, formState: { errors } } = useForm();

  // ── load profile ───────────────────────────────────────────────────────
  useEffect(() => {
    const load = async () => {
      try {
        setFetchError(null);
        // GET /api/v1/doctors/profile  — authenticated doctor endpoint
        const res = await doctorApi.getProfile();
        const data = res.data;
        setProfile(data);
        // pre-fill form with current values
        reset({
          qualification:    data.qualification,
          experience:       data.experience,
          specialization:   data.specialization,
          consultation_fee: data.consultation_fee,
          clinic_address:   data.clinic_address,
        });
      } catch (error) {
        setFetchError('Could not load your profile. Please try again.');
        showError(error, 'Failed to load profile');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [reset]);

  // ── save handler ───────────────────────────────────────────────────────
  const onSubmit = async (data) => {
    try {
      setSaving(true);
      const res = await doctorApi.updateProfile({
        ...data,
        experience:       Number(data.experience),
        consultation_fee: Number(data.consultation_fee),
      });
      setProfile(res.data);            // update displayed data
      setEditMode(false);
      toast.success('Profile updated successfully!');
    } catch (error) {
      showError(error, 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  // ── guards ─────────────────────────────────────────────────────────────
  if (loading) return <LoadingSpinner message="Loading your profile..." />;

  if (fetchError) {
    return (
      <Box maxWidth={600} mx="auto" mt={4}>
        <Alert severity="error" sx={{ mb: 2 }}>{fetchError}</Alert>
        <Button onClick={() => window.location.reload()}>Retry</Button>
      </Box>
    );
  }

  // ── helpers ────────────────────────────────────────────────────────────
  const initials = profile?.full_name
    ? profile.full_name.split(' ').map((w) => w[0]).join('').slice(0, 2).toUpperCase()
    : 'DR';

  const specializationLabel =
    SPECIALIZATIONS.find((s) => s.value === profile?.specialization)?.label
    || profile?.specialization
    || '—';

  // ── render ─────────────────────────────────────────────────────────────
  return (
    <Box maxWidth={860} mx="auto">

      {/* ── Profile Header Card ── */}
      <Card
        sx={{
          mb: 3,
          background: 'linear-gradient(135deg, #1d4ed8 0%, #2563eb 50%, #3b82f6 100%)',
          color: '#fff',
          overflow: 'hidden',
          position: 'relative',
        }}
      >
        {/* decorative circles */}
        <Box sx={{
          position: 'absolute', width: 240, height: 240, borderRadius: '50%',
          bgcolor: 'rgba(255,255,255,0.06)', top: -60, right: -40,
        }} />
        <Box sx={{
          position: 'absolute', width: 160, height: 160, borderRadius: '50%',
          bgcolor: 'rgba(255,255,255,0.05)', bottom: -30, right: 120,
        }} />

        <CardContent sx={{ p: { xs: 3, md: 4 }, position: 'relative', zIndex: 1 }}>
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            spacing={3}
            alignItems={{ xs: 'center', sm: 'flex-start' }}
          >
            {/* Avatar */}
            <Avatar
              sx={{
                width: 96,
                height: 96,
                fontSize: 32,
                fontWeight: 800,
                bgcolor: 'rgba(255,255,255,0.2)',
                border: '3px solid rgba(255,255,255,0.4)',
                color: '#fff',
                flexShrink: 0,
              }}
            >
              {initials}
            </Avatar>

            {/* Name + meta */}
            <Stack spacing={0.75} flex={1} textAlign={{ xs: 'center', sm: 'left' }}>
              <Typography variant="h4" fontWeight={800} color="inherit">
                Dr. {profile?.full_name}
              </Typography>

              <Stack
                direction="row"
                spacing={1}
                flexWrap="wrap"
                justifyContent={{ xs: 'center', sm: 'flex-start' }}
              >
                <Chip
                  label={specializationLabel}
                  size="small"
                  sx={{
                    bgcolor: 'rgba(255,255,255,0.22)',
                    color: '#fff',
                    fontWeight: 700,
                    fontSize: '0.75rem',
                    border: '1px solid rgba(255,255,255,0.3)',
                  }}
                />
                <Chip
                  label={profile?.is_active ? 'Active' : 'Inactive'}
                  size="small"
                  sx={{
                    bgcolor: profile?.is_active ? 'rgba(22,163,74,0.35)' : 'rgba(220,38,38,0.35)',
                    color: '#fff',
                    fontWeight: 700,
                    fontSize: '0.75rem',
                    border: `1px solid ${profile?.is_active ? 'rgba(22,163,74,0.5)' : 'rgba(220,38,38,0.5)'}`,
                  }}
                />
              </Stack>

              <Stack
                direction={{ xs: 'column', sm: 'row' }}
                spacing={{ xs: 0.5, sm: 2 }}
                mt={0.5}
                alignItems={{ xs: 'center', sm: 'flex-start' }}
              >
                <Stack direction="row" spacing={0.5} alignItems="center">
                  <EmailIcon sx={{ fontSize: 14, opacity: 0.8 }} />
                  <Typography variant="caption" sx={{ opacity: 0.9 }}>
                    {profile?.user_id ? profile.email || 'Registered Doctor' : '—'}
                  </Typography>
                </Stack>
                <Stack direction="row" spacing={0.5} alignItems="center">
                  <PhoneIcon sx={{ fontSize: 14, opacity: 0.8 }} />
                  <Typography variant="caption" sx={{ opacity: 0.9 }}>
                    {profile?.phone || '—'}
                  </Typography>
                </Stack>
                <Stack direction="row" spacing={0.5} alignItems="center">
                  <WorkIcon sx={{ fontSize: 14, opacity: 0.8 }} />
                  <Typography variant="caption" sx={{ opacity: 0.9 }}>
                    {profile?.experience} yrs experience
                  </Typography>
                </Stack>
              </Stack>
            </Stack>

            {/* Edit toggle */}
            <Box flexShrink={0}>
              {!editMode ? (
                <Button
                  variant="outlined"
                  startIcon={<EditIcon />}
                  onClick={() => setEditMode(true)}
                  sx={{
                    color: '#fff',
                    borderColor: 'rgba(255,255,255,0.5)',
                    '&:hover': {
                      borderColor: '#fff',
                      bgcolor: 'rgba(255,255,255,0.1)',
                    },
                  }}
                >
                  Edit Profile
                </Button>
              ) : (
                <Button
                  variant="outlined"
                  onClick={() => { setEditMode(false); reset(); }}
                  sx={{
                    color: '#fff',
                    borderColor: 'rgba(255,255,255,0.5)',
                    '&:hover': { borderColor: '#fff', bgcolor: 'rgba(255,255,255,0.1)' },
                  }}
                >
                  Cancel
                </Button>
              )}
            </Box>
          </Stack>
        </CardContent>
      </Card>

      {/* ── READ-ONLY VIEW ── */}
      {!editMode && (
        <Grid container spacing={3}>
          {/* Professional Details */}
          <Grid item xs={12} md={6}>
            <Card sx={{ height: '100%' }}>
              <CardContent sx={{ p: 3 }}>
                <Stack direction="row" spacing={1} alignItems="center" mb={2.5}>
                  <SchoolIcon color="primary" />
                  <Typography variant="h6" fontWeight={700}>Professional Details</Typography>
                </Stack>
                <Stack spacing={2.5} divider={<Divider />}>
                  <InfoRow icon={<SchoolIcon />} label="Qualification"  value={profile?.qualification} />
                  <InfoRow icon={<WorkIcon />}   label="Experience"     value={`${profile?.experience} years`} />
                  <InfoRow icon={<HospitalIcon />} label="Specialization" value={specializationLabel} />
                  <InfoRow icon={<BadgeIcon />}  label="License No."   value={profile?.license_number} />
                </Stack>
              </CardContent>
            </Card>
          </Grid>

          {/* Practice Info */}
          <Grid item xs={12} md={6}>
            <Card sx={{ height: '100%' }}>
              <CardContent sx={{ p: 3 }}>
                <Stack direction="row" spacing={1} alignItems="center" mb={2.5}>
                  <LocationIcon color="primary" />
                  <Typography variant="h6" fontWeight={700}>Practice Information</Typography>
                </Stack>
                <Stack spacing={2.5} divider={<Divider />}>
                  <InfoRow icon={<FeeIcon />}      label="Consultation Fee" value={formatCurrency(profile?.consultation_fee)} />
                  <InfoRow icon={<LocationIcon />} label="Clinic Address"   value={profile?.clinic_address} />
                  <InfoRow icon={<PhoneIcon />}    label="Phone"            value={profile?.phone} />
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* ── EDIT FORM ── */}
      {editMode && (
        <Card>
          <CardContent sx={{ p: 3 }}>
            <Stack direction="row" spacing={1} alignItems="center" mb={3}>
              <EditIcon color="primary" />
              <Typography variant="h6" fontWeight={700}>Edit Professional Information</Typography>
            </Stack>

            <Box component="form" onSubmit={handleSubmit(onSubmit)}>
              <Grid container spacing={2.5}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Qualification"
                    fullWidth
                    {...register('qualification', { required: 'Required' })}
                    error={!!errors.qualification}
                    helperText={errors.qualification?.message}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Experience (years)"
                    type="number"
                    fullWidth
                    inputProps={{ min: 0, max: 60 }}
                    {...register('experience', { required: 'Required', min: 0 })}
                    error={!!errors.experience}
                    helperText={errors.experience?.message}
                  />
                </Grid>

                {/* Specialization – controlled select so reset() works */}
                <Grid item xs={12} sm={6}>
                  <Controller
                    name="specialization"
                    control={control}
                    rules={{ required: 'Required' }}
                    render={({ field }) => (
                      <TextField
                        select
                        label="Specialization"
                        fullWidth
                        {...field}
                        error={!!errors.specialization}
                        helperText={errors.specialization?.message}
                      >
                        {SPECIALIZATIONS.map((s) => (
                          <MenuItem key={s.value} value={s.value}>
                            {s.label}
                          </MenuItem>
                        ))}
                      </TextField>
                    )}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Consultation Fee (₹)"
                    type="number"
                    fullWidth
                    inputProps={{ min: 0 }}
                    {...register('consultation_fee', { required: 'Required', min: 0 })}
                    error={!!errors.consultation_fee}
                    helperText={errors.consultation_fee?.message}
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    label="Clinic Address"
                    fullWidth
                    multiline
                    rows={2}
                    {...register('clinic_address', { required: 'Required' })}
                    error={!!errors.clinic_address}
                    helperText={errors.clinic_address?.message}
                  />
                </Grid>

                <Grid item xs={12}>
                  <Stack direction="row" spacing={2} justifyContent="flex-end">
                    <Button
                      variant="outlined"
                      onClick={() => { setEditMode(false); reset(); }}
                    >
                      Cancel
                    </Button>
                    <Button
                      type="submit"
                      startIcon={saving ? <CircularProgress size={16} color="inherit" /> : <SaveIcon />}
                      disabled={saving}
                    >
                      {saving ? 'Saving…' : 'Save Changes'}
                    </Button>
                  </Stack>
                </Grid>
              </Grid>
            </Box>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default DoctorProfilePage;
