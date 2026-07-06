import React from 'react';
import { Box, InputAdornment, TextField } from '@mui/material';
import { Search as SearchIcon } from '@mui/icons-material';

const SearchBar = ({
  placeholder = 'Search...',
  value,
  onChange,
  onClear,
  fullWidth = true,
  sx,
}) => {
  return (
    <Box sx={sx}>
      <TextField
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        fullWidth={fullWidth}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon color="action" />
            </InputAdornment>
          ),
        }}
        sx={{
          '& .MuiOutlinedInput-root': {
            borderRadius: 2,
          },
        }}
      />
    </Box>
  );
};

export default SearchBar;
