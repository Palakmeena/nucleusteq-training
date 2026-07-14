import React from 'react';
import { Button as MuiButton } from '@mui/material';
import { CircularProgress } from '@mui/material';

const Button = ({
  children,
  variant = 'contained',
  color = 'primary',
  size = 'medium',
  loading = false,
  disabled,
  startIcon,
  endIcon,
  fullWidth = false,
  sx,
  ...props
}) => {
  return (
    <MuiButton
      variant={variant}
      color={color}
      size={size}
      disabled={disabled || loading}
      startIcon={loading ? <CircularProgress size={20} /> : startIcon}
      endIcon={endIcon}
      fullWidth={fullWidth}
      sx={{
        textTransform: 'none',
        borderRadius: 2,
        fontWeight: 600,
        px: 3,
        py: 1,
        ...sx,
      }}
      {...props}
    >
      {children}
    </MuiButton>
  );
};

export default Button;