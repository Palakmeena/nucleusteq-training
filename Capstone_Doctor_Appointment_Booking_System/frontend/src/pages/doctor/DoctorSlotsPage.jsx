import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Box, Grid, Card, CardContent, Stack, Typography } from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  AccessTime as TimeIcon,
  EventAvailable as SlotIcon,
} from '@mui/icons-material';
import dayjs from 'dayjs';
import { toast } from 'react-toastify';
import PageHeader from '../../components/common/PageHeader';
import Modal from '../../components/dialogs/Modal';
import ConfirmationDialog from '../../components/dialogs/ConfirmationDialog';
import Button from '../../components/buttons/Button';
import Input from '../../components/inputs/Input';
import EmptyState from '../../components/emptyState/EmptyState';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import slotApi from '../../api/slotApi';
import { useDoctorProfileId } from '../../hooks/useDoctorProfile';
import { showError } from '../../utils/errorHandler';
import { formatDate } from '../../utils/formatters';

const DoctorSlotsPage = () => {
  const { doctorId, loading: profileLoading } = useDoctorProfileId();
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingSlot, setEditingSlot] = useState(null);
  const [deleteId, setDeleteId] = useState(null);
  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      date: dayjs().format('YYYY-MM-DD'),
      start_time: '09:00',
      end_time: '09:30',
    },
  });

  const fetchSlots = async () => {
    if (!doctorId) return;
    try {
      setLoading(true);
      const res = await slotApi.getSlotsByDoctor(doctorId);
      setSlots(res.data || []);
    } catch (error) {
      showError(error, 'Failed to load slots');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (doctorId) fetchSlots();
    else if (!profileLoading) setLoading(false);
  }, [doctorId, profileLoading]);

  const openCreate = () => {
    setEditingSlot(null);
    reset({
      date: dayjs().format('YYYY-MM-DD'),
      start_time: '09:00',
      end_time: '09:30',
    });
    setModalOpen(true);
  };

  const openEdit = (slot) => {
    setEditingSlot(slot);
    reset({
      date: slot.date,
      start_time: slot.start_time,
      end_time: slot.end_time,
    });
    setModalOpen(true);
  };

  const onSubmit = async (data) => {
    try {
      if (editingSlot) {
        await slotApi.updateSlot(editingSlot.id, data);
        toast.success('Slot updated');
      } else {
        await slotApi.createSlot(data);
        toast.success('Slot created');
      }
      setModalOpen(false);
      fetchSlots();
    } catch (error) {
      showError(error, 'Failed to save slot');
    }
  };

  const handleDelete = async () => {
    try {
      await slotApi.deleteSlot(deleteId);
      toast.success('Slot deleted');
      setDeleteId(null);
      fetchSlots();
    } catch (error) {
      showError(error, 'Failed to delete slot');
    }
  };

  if (profileLoading) return <LoadingSpinner message="Loading profile..." />;

  return (
    <Box>
      <PageHeader
        title="Manage Slots"
        subtitle="Set your availability for patient bookings"
        action={
          <Button startIcon={<AddIcon />} onClick={openCreate}>
            Add Slot
          </Button>
        }
      />

      {loading ? (
        <LoadingSpinner />
      ) : slots.length > 0 ? (
        <Grid container spacing={2}>
          {slots.map((slot) => (
            <Grid size={{ xs: 12, md: 6 }} key={slot.id}>
              <Card>
                <CardContent>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <Box
                      sx={{
                        width: 48,
                        height: 48,
                        borderRadius: 2,
                        bgcolor: slot.is_booked ? 'error.light' : 'success.light',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <TimeIcon color={slot.is_booked ? 'error' : 'success'} />
                    </Box>
                    <Stack flex={1}>
                      <Typography fontWeight={700}>
                        {slot.start_time} – {slot.end_time}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {formatDate(slot.date)} · {slot.is_booked ? 'Booked' : 'Available'}
                      </Typography>
                    </Stack>
                    {!slot.is_booked && (
                      <Stack direction="row" spacing={1}>
                        <Button size="small" startIcon={<EditIcon />} onClick={() => openEdit(slot)}>
                          Edit
                        </Button>
                        <Button
                          size="small"
                          color="error"
                          startIcon={<DeleteIcon />}
                          onClick={() => setDeleteId(slot.id)}
                        >
                          Delete
                        </Button>
                      </Stack>
                    )}
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      ) : (
        <EmptyState
          icon={SlotIcon}
          title="No slots yet"
          description="Create your first availability slot"
          action
          actionText="Add Slot"
          onAction={openCreate}
        />
      )}

      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingSlot ? 'Edit Slot' : 'New Slot'}
      >
        <Box component="form" onSubmit={handleSubmit(onSubmit)}>
          <Stack spacing={2} py={1}>
            <Input
              label="Date"
              type="date"
              InputLabelProps={{ shrink: true }}
              {...register('date', { required: true })}
            />
            <Input
              label="Start Time"
              type="time"
              InputLabelProps={{ shrink: true }}
              {...register('start_time', { required: true })}
            />
            <Input
              label="End Time"
              type="time"
              InputLabelProps={{ shrink: true }}
              {...register('end_time', { required: true })}
            />
          </Stack>
          <Stack direction="row" spacing={2} justifyContent="flex-end" mt={2}>
            <Button variant="outlined" onClick={() => setModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit">{editingSlot ? 'Update' : 'Create'}</Button>
          </Stack>
        </Box>
      </Modal>

      <ConfirmationDialog
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Slot"
        message="This slot will be permanently removed."
        confirmText="Delete"
        severity="error"
      />
    </Box>
  );
};

export default DoctorSlotsPage;
