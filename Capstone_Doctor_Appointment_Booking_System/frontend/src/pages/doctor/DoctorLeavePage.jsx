import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import {
  Box, Card, CardContent, Grid, Stack, Typography, TextField,
  Chip, Divider, Alert, alpha,
} from '@mui/material';
import {
  BeachAccess as LeaveIcon,
  PowerSettingsNew as ReactivateIcon,
  Add as AddIcon,
  CalendarToday as CalendarIcon,
  CheckCircleOutlined as CheckIcon,
  PendingOutlined as PendingIcon,
  CancelOutlined as RejectedIcon,
  History as HistoryIcon,
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import PageHeader from '../../components/common/PageHeader';
import Button from '../../components/buttons/Button';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import EmptyState from '../../components/emptyState/EmptyState';
import deactivationApi from '../../api/deactivationApi';
import { showError } from '../../utils/errorHandler';

const STATUS_CONFIG = {
  PENDING: {
    color: 'warning',
    icon: <PendingIcon fontSize="small" />,
    bgColor: 'rgba(245, 158, 11, 0.08)',
    borderColor: 'rgba(245, 158, 11, 0.3)',
  },
  APPROVED: {
    color: 'success',
    icon: <CheckIcon fontSize="small" />,
    bgColor: 'rgba(22, 163, 74, 0.08)',
    borderColor: 'rgba(22, 163, 74, 0.3)',
  },
  REJECTED: {
    color: 'error',
    icon: <RejectedIcon fontSize="small" />,
    bgColor: 'rgba(220, 38, 38, 0.08)',
    borderColor: 'rgba(220, 38, 38, 0.3)',
  },
};

const DoctorLeavePage = () => {
  const location = useLocation();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [reactivating, setReactivating] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    start_date: '',
    end_date: '',
    reason: '',
  });
  const [errors, setErrors] = useState({});

  const today = new Date().toISOString().split('T')[0];
  const appointmentDate = location.state?.appointmentDate;

  useEffect(() => {
    if (!appointmentDate) return;
    setShowForm(true);
    setForm((current) => ({
      ...current,
      start_date: current.start_date || appointmentDate,
      end_date: current.end_date || appointmentDate,
    }));
  }, [appointmentDate]);

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const res = await deactivationApi.getMyRequests();
      setRequests(res.data || []);
    } catch (error) {
      showError(error, 'Failed to load requests');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const validate = () => {
    const errs = {};
    if (!form.start_date) errs.start_date = 'Start date is required';
    if (!form.end_date) errs.end_date = 'End date is required';
    if (form.start_date && form.end_date && form.start_date > form.end_date) {
      errs.end_date = 'End date must be on or after start date';
    }
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    try {
      setSubmitting(true);
      await deactivationApi.submitRequest({
        start_date: form.start_date,
        end_date: form.end_date,
        reason: form.reason || undefined,
      });
      toast.success('Leave request submitted successfully. Awaiting admin approval.');
      setForm({ start_date: '', end_date: '', reason: '' });
      setShowForm(false);
      fetchRequests();
    } catch (error) {
      showError(error, 'Failed to submit request');
    } finally {
      setSubmitting(false);
    }
  };

  const handleReactivate = async () => {
    try {
      setReactivating(true);
      await deactivationApi.reactivateSelf();
      toast.success('You are now active again and visible to patients.');
      fetchRequests();
    } catch (error) {
      showError(error, 'Failed to reactivate');
    } finally {
      setReactivating(false);
    }
  };

  const hasPending = requests.some((r) => r.status === 'PENDING');

  return (
    <Box>
      <PageHeader
        title="Leave & Availability"
        subtitle="Request temporary deactivation or reactivate yourself"
      />

      <Grid container spacing={3}>
        {/* Left: Request Form */}
        <Grid size={{ xs: 12, md: 5 }}>
          <Stack spacing={3}>
            {/* Request Leave Card */}
            <Card
              sx={{
                border: '1px solid',
                borderColor: 'divider',
                boxShadow: '0 2px 12px rgba(0,0,0,0.06)',
              }}
            >
              <CardContent sx={{ p: 3 }}>
                <Stack direction="row" spacing={1.5} alignItems="center" mb={1.5}>
                  <Box
                    sx={{
                      width: 40,
                      height: 40,
                      borderRadius: 2,
                      bgcolor: 'warning.light',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}
                  >
                    <LeaveIcon sx={{ color: 'warning.dark', fontSize: 22 }} />
                  </Box>
                  <Box>
                    <Typography variant="h6" fontWeight={700}>
                      Request Leave
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      Temporary deactivation request
                    </Typography>
                  </Box>
                </Stack>

                {hasPending && (
                  <Alert
                    severity="info"
                    sx={{ mb: 2.5, borderRadius: 2, fontSize: '0.813rem' }}
                  >
                    You already have a pending request. Wait for admin review before submitting another.
                  </Alert>
                )}

                <Typography variant="body2" color="text.secondary" mb={2.5} lineHeight={1.6}>
                  Submit a temporary deactivation request. You will not be deactivated until the admin approves it.
                </Typography>

                {appointmentDate && (
                  <Alert severity="info" sx={{ mb: 2.5, borderRadius: 2 }}>
                    Your appointment date has been prefilled. Add a reason and submit your leave request.
                  </Alert>
                )}

                {!showForm ? (
                  <Button
                    startIcon={<AddIcon />}
                    fullWidth
                    onClick={() => setShowForm(true)}
                    disabled={hasPending}
                    id="show-leave-form-btn"
                    sx={{ py: 1.5, borderRadius: 2 }}
                  >
                    New Leave Request
                  </Button>
                ) : (
                  <Box component="form" onSubmit={handleSubmit}>
                    <Stack spacing={2.5}>
                      {/* Date fields with proper shrink label fix */}
                      <TextField
                        label="Start Date"
                        type="date"
                        value={form.start_date}
                        onChange={(e) => setForm({ ...form, start_date: e.target.value })}
                        error={!!errors.start_date}
                        helperText={errors.start_date}
                        inputProps={{ min: today }}
                        InputLabelProps={{ shrink: true }}
                        fullWidth
                        id="leave-start-date"
                        required
                        sx={{
                          '& .MuiInputLabel-root': {
                            background: 'white',
                            px: 0.5,
                          },
                        }}
                      />
                      <TextField
                        label="End Date"
                        type="date"
                        value={form.end_date}
                        onChange={(e) => setForm({ ...form, end_date: e.target.value })}
                        error={!!errors.end_date}
                        helperText={errors.end_date}
                        inputProps={{ min: form.start_date || today }}
                        InputLabelProps={{ shrink: true }}
                        fullWidth
                        id="leave-end-date"
                        required
                        sx={{
                          '& .MuiInputLabel-root': {
                            background: 'white',
                            px: 0.5,
                          },
                        }}
                      />
                      <TextField
                        label="Reason (Optional)"
                        multiline
                        rows={3}
                        value={form.reason}
                        onChange={(e) => setForm({ ...form, reason: e.target.value })}
                        placeholder="e.g. Medical leave, personal travel..."
                        fullWidth
                        id="leave-reason"
                        InputLabelProps={{ shrink: true }}
                      />
                      <Stack direction="row" spacing={2}>
                        <Button
                          type="submit"
                          loading={submitting}
                          fullWidth
                          id="submit-leave-btn"
                          sx={{ py: 1.25, borderRadius: 2 }}
                        >
                          Submit Request
                        </Button>
                        <Button
                          variant="outlined"
                          color="secondary"
                          onClick={() => { setShowForm(false); setErrors({}); }}
                          fullWidth
                          id="cancel-leave-btn"
                          sx={{ py: 1.25, borderRadius: 2 }}
                        >
                          Cancel
                        </Button>
                      </Stack>
                    </Stack>
                  </Box>
                )}
              </CardContent>
            </Card>

            {/* Reactivate Card */}
            <Card
              sx={{
                border: '1px solid',
                borderColor: 'divider',
                boxShadow: '0 2px 12px rgba(0,0,0,0.06)',
              }}
            >
              <CardContent sx={{ p: 3 }}>
                <Stack direction="row" spacing={1.5} alignItems="center" mb={1.5}>
                  <Box
                    sx={{
                      width: 40,
                      height: 40,
                      borderRadius: 2,
                      bgcolor: 'success.light',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}
                  >
                    <ReactivateIcon sx={{ color: 'success.dark', fontSize: 22 }} />
                  </Box>
                  <Box>
                    <Typography variant="h6" fontWeight={700}>
                      Reactivate Now
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      Instant reactivation
                    </Typography>
                  </Box>
                </Stack>
                <Typography variant="body2" color="text.secondary" mb={2.5} lineHeight={1.6}>
                  If you are currently deactivated, you can reactivate yourself immediately — no admin approval required.
                </Typography>
                <Button
                  variant="outlined"
                  color="success"
                  loading={reactivating}
                  fullWidth
                  onClick={handleReactivate}
                  id="reactivate-self-btn"
                  sx={{ py: 1.5, borderRadius: 2 }}
                >
                  Reactivate My Account
                </Button>
              </CardContent>
            </Card>
          </Stack>
        </Grid>

        {/* Right: My Requests */}
        <Grid size={{ xs: 12, md: 7 }}>
          <Card
            sx={{
              height: '100%',
              border: '1px solid',
              borderColor: 'divider',
              boxShadow: '0 2px 12px rgba(0,0,0,0.06)',
            }}
          >
            <CardContent sx={{ p: 3 }}>
              <Stack direction="row" spacing={1.5} alignItems="center" mb={3}>
                <HistoryIcon color="primary" />
                <Typography variant="h6" fontWeight={700}>
                  My Leave Requests
                </Typography>
                {requests.length > 0 && (
                  <Chip
                    label={requests.length}
                    size="small"
                    color="primary"
                    sx={{ fontWeight: 700, ml: 'auto' }}
                  />
                )}
              </Stack>

              {loading ? (
                <LoadingSpinner />
              ) : requests.length === 0 ? (
                <EmptyState
                  title="No requests yet"
                  description="Submit a leave request to get started"
                />
              ) : (
                <Stack spacing={2}>
                  {requests
                    .slice()
                    .sort((a, b) => new Date(b.created_at) - new Date(a.created_at))
                    .map((req) => {
                      const cfg = STATUS_CONFIG[req.status] || {};
                      return (
                        <Box
                          key={req.id}
                          sx={{
                            p: 2.5,
                            border: '1px solid',
                            borderColor: cfg.borderColor || 'divider',
                            borderRadius: 2.5,
                            bgcolor: cfg.bgColor || 'background.paper',
                            transition: 'all 0.2s',
                            '&:hover': {
                              boxShadow: '0 4px 16px rgba(0,0,0,0.10)',
                              transform: 'translateY(-1px)',
                            },
                          }}
                        >
                          <Stack direction="row" justifyContent="space-between" alignItems="flex-start">
                            <Stack spacing={1} flex={1}>
                              {/* Date range with calendar icons */}
                              <Stack direction="row" spacing={1} alignItems="center">
                                <CalendarIcon sx={{ fontSize: 16, color: 'primary.main' }} />
                                <Typography variant="body2" fontWeight={700} color="text.primary">
                                  {new Date(req.start_date).toLocaleDateString('en-IN', {
                                    day: '2-digit', month: 'short', year: 'numeric',
                                  })}
                                </Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mx: 0.5 }}>→</Typography>
                                <Typography variant="body2" fontWeight={700} color="text.primary">
                                  {new Date(req.end_date).toLocaleDateString('en-IN', {
                                    day: '2-digit', month: 'short', year: 'numeric',
                                  })}
                                </Typography>
                              </Stack>

                              {req.reason && (
                                <Typography
                                  variant="body2"
                                  color="text.secondary"
                                  sx={{
                                    bgcolor: 'rgba(0,0,0,0.04)',
                                    px: 1.5,
                                    py: 0.75,
                                    borderRadius: 1.5,
                                    fontStyle: 'italic',
                                  }}
                                >
                                  "{req.reason}"
                                </Typography>
                              )}

                              <Typography variant="caption" color="text.disabled">
                                Submitted:{' '}
                                {new Date(req.created_at).toLocaleDateString('en-IN', {
                                  day: '2-digit',
                                  month: 'short',
                                  year: 'numeric',
                                })}
                              </Typography>

                              {req.reviewed_at && (
                                <Typography variant="caption" color="text.disabled">
                                  Reviewed:{' '}
                                  {new Date(req.reviewed_at).toLocaleDateString('en-IN', {
                                    day: '2-digit',
                                    month: 'short',
                                    year: 'numeric',
                                  })}
                                </Typography>
                              )}
                            </Stack>

                            <Chip
                              icon={cfg.icon}
                              label={req.status}
                              color={STATUS_CONFIG[req.status]?.color || 'default'}
                              size="small"
                              sx={{ fontWeight: 700, ml: 1.5, mt: 0.25 }}
                            />
                          </Stack>
                        </Box>
                      );
                    })}
                </Stack>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DoctorLeavePage;
