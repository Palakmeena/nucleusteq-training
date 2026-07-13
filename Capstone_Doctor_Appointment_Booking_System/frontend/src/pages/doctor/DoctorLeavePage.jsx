import React, { useEffect, useState } from 'react';
import {
  Box, Card, CardContent, Grid, Stack, Typography, TextField,
  Chip, Divider, Alert,
} from '@mui/material';
import {
  BeachAccess as LeaveIcon,
  PowerSettingsNew as ReactivateIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import PageHeader from '../../components/common/PageHeader';
import Button from '../../components/buttons/Button';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import EmptyState from '../../components/emptyState/EmptyState';
import deactivationApi from '../../api/deactivationApi';
import { showError } from '../../utils/errorHandler';

const STATUS_COLOR = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'error',
};

const DoctorLeavePage = () => {
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
        <Grid item xs={12} md={5}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Stack direction="row" spacing={1.5} alignItems="center" mb={3}>
                <LeaveIcon color="warning" />
                <Typography variant="h6" fontWeight={700}>
                  Request Leave
                </Typography>
              </Stack>

              {hasPending && (
                <Alert severity="info" sx={{ mb: 2, borderRadius: 2 }}>
                  You already have a pending request. Wait for admin review before submitting another.
                </Alert>
              )}

              <Typography variant="body2" color="text.secondary" mb={3}>
                Submit a temporary deactivation request. You will not be deactivated until the admin approves it.
              </Typography>

              {!showForm ? (
                <Button
                  startIcon={<AddIcon />}
                  fullWidth
                  onClick={() => setShowForm(true)}
                  disabled={hasPending}
                  id="show-leave-form-btn"
                  sx={{ py: 1.5 }}
                >
                  New Leave Request
                </Button>
              ) : (
                <Box component="form" onSubmit={handleSubmit}>
                  <Stack spacing={2.5}>
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
                    />
                    <Stack direction="row" spacing={2}>
                      <Button
                        type="submit"
                        loading={submitting}
                        fullWidth
                        id="submit-leave-btn"
                      >
                        Submit Request
                      </Button>
                      <Button
                        variant="outlined"
                        color="secondary"
                        onClick={() => { setShowForm(false); setErrors({}); }}
                        fullWidth
                        id="cancel-leave-btn"
                      >
                        Cancel
                      </Button>
                    </Stack>
                  </Stack>
                </Box>
              )}

              <Divider sx={{ my: 3 }} />

              <Stack direction="row" spacing={1.5} alignItems="center" mb={2}>
                <ReactivateIcon color="success" />
                <Typography variant="h6" fontWeight={700}>
                  Reactivate Now
                </Typography>
              </Stack>
              <Typography variant="body2" color="text.secondary" mb={2}>
                If you are currently deactivated, you can reactivate yourself immediately — no admin approval required.
              </Typography>
              <Button
                variant="outlined"
                color="success"
                loading={reactivating}
                fullWidth
                onClick={handleReactivate}
                id="reactivate-self-btn"
                sx={{ py: 1.5 }}
              >
                Reactivate My Account
              </Button>
            </CardContent>
          </Card>
        </Grid>

        {/* Right: My Requests */}
        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={700} mb={3}>
                My Leave Requests
              </Typography>

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
                    .map((req) => (
                      <Card
                        key={req.id}
                        sx={{
                          p: 2,
                          border: '1px solid',
                          borderColor: 'divider',
                          borderRadius: 2,
                          transition: 'all 0.2s',
                          '&:hover': { borderColor: 'primary.main', boxShadow: 2 },
                        }}
                      >
                        <Stack direction="row" justifyContent="space-between" alignItems="flex-start">
                          <Box flex={1}>
                            <Stack direction="row" spacing={1} alignItems="center" mb={0.5}>
                              <Typography variant="body2" fontWeight={700}>
                                {req.start_date}
                              </Typography>
                              <Typography variant="body2" color="text.secondary">→</Typography>
                              <Typography variant="body2" fontWeight={700}>
                                {req.end_date}
                              </Typography>
                            </Stack>
                            {req.reason && (
                              <Typography variant="body2" color="text.secondary" mb={0.5}>
                                {req.reason}
                              </Typography>
                            )}
                            <Typography variant="caption" color="text.secondary">
                              Submitted: {new Date(req.created_at).toLocaleDateString('en-IN', {
                                day: '2-digit',
                                month: 'short',
                                year: 'numeric',
                              })}
                            </Typography>
                          </Box>
                          <Chip
                            label={req.status}
                            color={STATUS_COLOR[req.status] || 'default'}
                            size="small"
                            sx={{ fontWeight: 700, ml: 1 }}
                          />
                        </Stack>
                        {req.reviewed_at && (
                          <Typography variant="caption" color="text.secondary" display="block" mt={1}>
                            Reviewed: {new Date(req.reviewed_at).toLocaleDateString('en-IN', {
                              day: '2-digit',
                              month: 'short',
                              year: 'numeric',
                            })}
                          </Typography>
                        )}
                      </Card>
                    ))}
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
