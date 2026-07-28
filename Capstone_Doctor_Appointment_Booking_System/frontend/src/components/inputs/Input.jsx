import React from 'react';
import { TextField as MuiTextField } from '@mui/material';

const Input = ({
  label,
  name,
  value,
  onChange,
  error,
  helperText,
  type = 'text',
  placeholder,
  required = false,
  disabled = false,
  fullWidth = true,
  multiline = false,
  rows = 4,
  sx,
  ...props
}) => {
  return (
    <MuiTextField
      label={label}
      name={name}
      value={value}
      onChange={onChange}
      error={!!error}
      helperText={helperText}
      type={type}
      placeholder={placeholder}
      required={required}
      disabled={disabled}
      fullWidth={fullWidth}
      multiline={multiline}
      rows={rows}
      sx={{
        '& .MuiOutlinedInput-root': {
          borderRadius: 2,
        },
        ...sx,
      }}
      {...props}
    />
  );
};

export default Input;
