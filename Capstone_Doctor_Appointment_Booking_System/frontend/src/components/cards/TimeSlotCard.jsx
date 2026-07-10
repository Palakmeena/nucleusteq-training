import React from 'react';
import { Card, CardContent, Stack, Typography } from '@mui/material';
import { AccessTime as TimeIcon } from '@mui/icons-material';

const TimeSlotCard = ({ slot, selected, onSelect, disabled }) => (
  <Card
    onClick={disabled ? undefined : () => onSelect(slot)}
    sx={{
      cursor: disabled ? 'not-allowed' : 'pointer',
      opacity: disabled ? 0.5 : 1,
      border: '2px solid',
      borderColor: selected ? 'primary.main' : 'divider',
      bgcolor: selected ? 'primary.light' : 'background.paper',
      transition: 'all 0.15s',
      '&:hover': disabled ? {} : { borderColor: 'primary.main' },
    }}
  >
    <CardContent sx={{ textAlign: 'center', py: 2 }}>
      <Stack spacing={0.5} alignItems="center">
        <TimeIcon color="primary" fontSize="small" />
        <Typography variant="subtitle1" fontWeight={700}>
          {slot.start_time}
        </Typography>
        <Typography variant="caption" color="text.secondary">
          to {slot.end_time}
        </Typography>
        {disabled && (
          <Typography variant="caption" color="error" fontWeight={600}>
            Booked
          </Typography>
        )}
      </Stack>
    </CardContent>
  </Card>
);

export default TimeSlotCard;
