import React from 'react';
import {
  Card,
  CardContent,
  CardActions,
  Stack,
  Avatar,
  Typography,
  Button,
  Chip,
  Box,
} from '@mui/material';
import {
  LocationOn as LocationIcon,
  Work as WorkIcon,
  ArrowForward as ArrowIcon,
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
import { formatCurrency } from '../../utils/formatters';

const BADGE_COLORS = [
  { bg: '#eff6ff', color: '#1d4ed8' },
  { bg: '#f0fdf4', color: '#15803d' },
  { bg: '#fdf4ff', color: '#7e22ce' },
  { bg: '#fff7ed', color: '#c2410c' },
  { bg: '#f0fdfa', color: '#0f766e' },
  { bg: '#fef9c3', color: '#854d0e' },
];

const badgeColor = (spec = '') => BADGE_COLORS[spec.charCodeAt(0) % BADGE_COLORS.length];

const DoctorCard = ({ doctor }) => {
  const badge = badgeColor(doctor.specialization);

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: '0 12px 30px rgba(37,99,235,0.12), 0 4px 12px rgba(0,0,0,0.06)',
        },
      }}
    >
      {/* Body — grows to push actions to bottom */}
      <CardContent sx={{ p: 2.5, flexGrow: 1 }}>
        {/* Avatar + name */}
        <Stack direction="row" spacing={2} alignItems="center" mb={2}>
          <Avatar
            sx={{
              width: 56,
              height: 56,
              bgcolor: 'primary.main',
              fontSize: 22,
              fontWeight: 800,
              flexShrink: 0,
            }}
          >
            {doctor.full_name?.charAt(0) || 'D'}
          </Avatar>

          <Box overflow="hidden">
            <Typography
              variant="subtitle1"
              fontWeight={700}
              noWrap
              title={`Dr. ${doctor.full_name}`}
            >
              Dr. {doctor.full_name}
            </Typography>
            <Chip
              label={doctor.specialization}
              size="small"
              sx={{
                mt: 0.25,
                bgcolor: badge.bg,
                color: badge.color,
                fontWeight: 600,
                fontSize: '0.7rem',
                height: 20,
                border: `1px solid ${badge.color}22`,
              }}
            />
          </Box>
        </Stack>

        {/* Info rows */}
        <Stack spacing={1}>
          <Stack direction="row" spacing={0.75} alignItems="center">
            <LocationIcon sx={{ fontSize: 14, color: 'text.secondary', flexShrink: 0 }} />
            <Typography
              variant="caption"
              color="text.secondary"
              noWrap
              title={doctor.clinic_address}
            >
              {doctor.clinic_address}
            </Typography>
          </Stack>
          <Stack direction="row" spacing={0.75} alignItems="center">
            <WorkIcon sx={{ fontSize: 14, color: 'text.secondary', flexShrink: 0 }} />
            <Typography variant="caption" color="text.secondary">
              {doctor.experience} {doctor.experience === 1 ? 'yr' : 'yrs'} experience
            </Typography>
          </Stack>
        </Stack>

        {/* Fee */}
        <Box mt={2} pt={2} borderTop="1px solid" borderColor="divider">
          <Typography variant="caption" color="text.secondary" display="block">
            Consultation Fee
          </Typography>
          <Typography variant="subtitle1" fontWeight={700} color="primary.main">
            {formatCurrency(doctor.consultation_fee)}
          </Typography>
        </Box>
      </CardContent>

      {/* Single action — View Profile only */}
      <CardActions sx={{ px: 2.5, pb: 2.5, pt: 0 }}>
        <Button
          variant="contained"
          fullWidth
          endIcon={<ArrowIcon />}
          component={Link}
          to={`/doctor/${doctor.id}`}
          size="small"
        >
          View Profile & Book
        </Button>
      </CardActions>
    </Card>
  );
};

export default DoctorCard;
