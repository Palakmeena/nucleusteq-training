import React from 'react';
import { FormControl, InputLabel, Select as MuiSelect, MenuItem, FormHelperText } from '@mui/material';

const Select = ({
  label,
  name,
  value,
  onChange,
  error,
  helperText,
  options = [],
  required = false,
  disabled = false,
  fullWidth = true,
  sx,
  ...props
}) => {
  return (
    <FormControl fullWidth={fullWidth} error={!!error} sx={sx}>
      {label && <InputLabel>{label}</InputLabel>}
      <MuiSelect
        label={label}
        name={name}
        value={value}
        onChange={onChange}
        required={required}
        disabled={disabled}
        sx={{
          borderRadius: 2,
          ...sx,
        }}
        {...props}
      >
        {options.map((option) => (
          <MenuItem key={option.value} value={option.value}>
            {option.label}
          </MenuItem>
        ))}
      </MuiSelect>
      {helperText && <FormHelperText>{helperText}</FormHelperText>}
    </FormControl>
  );
};

export default Select;
