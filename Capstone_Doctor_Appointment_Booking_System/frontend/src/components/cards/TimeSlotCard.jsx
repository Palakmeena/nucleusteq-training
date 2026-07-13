import React from 'react';
import { Box, Typography, Stack } from '@mui/material';
import { CheckCircle as CheckIcon, Block as BlockIcon } from '@mui/icons-material';
import { formatTime } from '../../utils/formatters';

/**
 * Pill-style time slot selector.
 *
 * States:
 *  - available   → blue-outlined, clickable, hover fill
 *  - selected    → solid primary fill, check icon
 *  - booked      → grey, strikethrough text, not clickable
 */
const TimeSlotCard = ({ slot, selected, onSelect, disabled }) => {
  const handleClick = () => {
    if (!disabled && onSelect) onSelect(slot);
  };

  if (disabled) {
    // Booked — clearly unavailable
    return (
      <Box
        sx={{
          px: 2,
          py: 1.25,
          borderRadius: 2,
          border: '1px solid',
          borderColor: '#e2e8f0',
          bgcolor: '#f8fafc',
          textAlign: 'center',
          cursor: 'not-allowed',
          userSelect: 'none',
        }}
      >
        <Stack spacing={0.25} alignItems="center">
          <Typography
            variant="body2"
            fontWeight={600}
            sx={{ color: '#94a3b8', textDecoration: 'line-through' }}
          >
            {formatTime(slot.start_time)}
          </Typography>
          <Typography variant="caption" sx={{ color: '#94a3b8' }}>
            {formatTime(slot.end_time)}
          </Typography>
          <Stack direction="row" spacing={0.5} alignItems="center" mt={0.25}>
            <BlockIcon sx={{ fontSize: 10, color: '#94a3b8' }} />
            <Typography variant="caption" sx={{ color: '#94a3b8', fontSize: '0.6rem' }}>
              Booked
            </Typography>
          </Stack>
        </Stack>
      </Box>
    );
  }

  if (selected) {
    return (
      <Box
        onClick={handleClick}
        sx={{
          px: 2,
          py: 1.25,
          borderRadius: 2,
          border: '2px solid',
          borderColor: 'primary.main',
          bgcolor: 'primary.main',
          textAlign: 'center',
          cursor: 'pointer',
          userSelect: 'none',
          transition: 'all 0.15s',
          boxShadow: '0 4px 12px rgba(37,99,235,0.3)',
        }}
      >
        <Stack spacing={0.25} alignItems="center">
          <CheckIcon sx={{ fontSize: 14, color: '#fff', mb: 0.25 }} />
          <Typography variant="body2" fontWeight={700} sx={{ color: '#fff' }}>
            {formatTime(slot.start_time)}
          </Typography>
          <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.8)' }}>
            {formatTime(slot.end_time)}
          </Typography>
        </Stack>
      </Box>
    );
  }

  // Available — default
  return (
    <Box
      onClick={handleClick}
      sx={{
        px: 2,
        py: 1.25,
        borderRadius: 2,
        border: '1.5px solid',
        borderColor: '#dbeafe',
        bgcolor: '#f0f9ff',
        textAlign: 'center',
        cursor: 'pointer',
        userSelect: 'none',
        transition: 'all 0.15s',
        '&:hover': {
          borderColor: 'primary.main',
          bgcolor: '#dbeafe',
          transform: 'translateY(-1px)',
          boxShadow: '0 4px 8px rgba(37,99,235,0.15)',
        },
      }}
    >
      <Stack spacing={0.25} alignItems="center">
        <Typography variant="body2" fontWeight={600} color="primary.dark">
          {formatTime(slot.start_time)}
        </Typography>
        <Typography variant="caption" color="primary.main">
          {formatTime(slot.end_time)}
        </Typography>
      </Stack>
    </Box>
  );
};

export default TimeSlotCard;
