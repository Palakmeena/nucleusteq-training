import React from 'react';
import { Card, CardContent, Stack, Avatar, Typography, Button, Divider } from '@mui/material';
import {
  LocationOn as LocationIcon,
  CalendarMonth as CalendarIcon,
  MedicalServices as MedicalIcon,
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
import { formatCurrency } from '../../utils/formatters';

const DoctorCard = ({ doctor }) => (
  <Card
    sx={{
      height: '100%',
      transition: 'transform 0.2s',
      '&:hover': { transform: 'translateY(-4px)' },
    }}
  >
    <CardContent>
      <Stack direction="row" spacing={2.5}>
        <Avatar sx={{ width: 72, height: 72, bgcolor: 'primary.main' }}>
          <MedicalIcon sx={{ fontSize: 36 }} />
        </Avatar>
        <Stack spacing={0.5} flex={1}>
          <Typography variant="h6" fontWeight={700}>
            Dr. {doctor.full_name}
          </Typography>
          <Typography variant="body2" color="primary.main" fontWeight={500}>
            {doctor.specialization}
          </Typography>
          <Stack direction="row" spacing={0.5} alignItems="center">
            <LocationIcon fontSize="small" color="action" />
            <Typography variant="caption" color="text.secondary" noWrap>
              {doctor.clinic_address}
            </Typography>
          </Stack>
          <Typography variant="body2" color="text.secondary">
            {doctor.experience} yrs experience
          </Typography>
          <Typography variant="subtitle1" color="primary.main" fontWeight={700}>
            {formatCurrency(doctor.consultation_fee)}
          </Typography>
        </Stack>
      </Stack>

      <Divider sx={{ my: 2 }} />

      <Stack direction="row" spacing={1.5}>
        <Button
          variant="contained"
          fullWidth
          startIcon={<CalendarIcon />}
          component={Link}
          to={`/doctor/${doctor.id}`}
        >
          Book Appointment
        </Button>
        <Button variant="outlined" component={Link} to={`/doctor/${doctor.id}`}>
          Profile
        </Button>
      </Stack>
    </CardContent>
  </Card>
);

export default DoctorCard;