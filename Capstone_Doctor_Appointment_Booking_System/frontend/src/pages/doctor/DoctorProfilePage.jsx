import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Box, Card, CardContent, Stack, MenuItem } from '@mui/material';
import { toast } from 'react-toastify';
import PageHeader from '../../components/common/PageHeader';
import Input from '../../components/inputs/Input';
import Button from '../../components/buttons/Button';
import LoadingSpinner from '../../components/loading/LoadingSpinner';
import doctorApi from '../../api/doctorApi';
import { useDoctorProfileId } from '../../hooks/useDoctorProfile';
import { SPECIALIZATIONS } from '../../constants/specializations';
import { showError } from '../../utils/errorHandler';

const DoctorProfilePage = () => {
  const { doctorId, loading: idLoading } = useDoctorProfileId();
  const [loading, setLoading] = useState(true);
  const { register, handleSubmit, reset } = useForm();

  useEffect(() => {
    const load = async () => {
      if (!doctorId) {
        setLoading(false);
        return;
      }
      try {
        const res = await doctorApi.getDoctorById(doctorId);
        reset({
          qualification: res.data.qualification,
          experience: res.data.experience,
          specialization: res.data.specialization,
          consultation_fee: res.data.consultation_fee,
          clinic_address: res.data.clinic_address,
        });
      } catch (error) {
        showError(error, 'Failed to load profile');
      } finally {
        setLoading(false);
      }
    };
    if (!idLoading) load();
  }, [doctorId, idLoading, reset]);

  const onSubmit = async (data) => {
    try {
      await doctorApi.updateProfile({
        ...data,
        experience: Number(data.experience),
        consultation_fee: Number(data.consultation_fee),
      });
      toast.success('Profile updated successfully');
    } catch (error) {
      showError(error, 'Failed to update profile');
    }
  };

  if (idLoading || loading) return <LoadingSpinner message="Loading profile..." />;

  return (
    <Box maxWidth={720}>
      <PageHeader title="My Profile" subtitle="Update your professional information" />

      <Card>
        <CardContent>
          <Box component="form" onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={2.5}>
              <Input label="Qualification" {...register('qualification')} />
              <Input label="Experience (years)" type="number" {...register('experience')} />
              <Input label="Specialization" select {...register('specialization')}>
                {SPECIALIZATIONS.map((s) => (
                  <MenuItem key={s} value={s}>
                    {s}
                  </MenuItem>
                ))}
              </Input>
              <Input label="Consultation Fee" type="number" {...register('consultation_fee')} />
              <Input label="Clinic Address" {...register('clinic_address')} />
              <Button type="submit">Save Changes</Button>
            </Stack>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default DoctorProfilePage;