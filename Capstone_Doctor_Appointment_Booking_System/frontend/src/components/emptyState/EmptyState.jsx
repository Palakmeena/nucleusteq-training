import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import { Inbox as InboxIcon } from '@mui/icons-material';

const EmptyState = ({
  icon,
  title = 'No data found',
  description,
  action,
  actionText,
  onAction,
  sx,
}) => {
  const IconComponent = icon || InboxIcon;

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        py: 8,
        px: 4,
        ...sx,
      }}
    >
      <IconComponent
        sx={{
          fontSize: 80,
          color: 'text.disabled',
          mb: 2,
        }}
      />
      <Typography
        variant="h6"
        color="text.secondary"
        sx={{ mb: 1, fontWeight: 500 }}
      >
        {title}
      </Typography>
      {description && (
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ mb: 3, textAlign: 'center', maxWidth: 400 }}
        >
          {description}
        </Typography>
      )}
      {action && (
        <Button variant="contained" onClick={onAction}>
          {actionText}
        </Button>
      )}
    </Box>
  );
};

export default EmptyState;
