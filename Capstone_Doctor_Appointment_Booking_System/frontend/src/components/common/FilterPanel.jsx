import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  FormControl,
  Select,
  MenuItem,
  Button,
  Stack,
  Divider,
} from '@mui/material';
import { Star as StarIcon } from '@mui/icons-material';
import { SPECIALIZATIONS } from '../../constants/specializations';

const FilterPanel = ({ filters, onChange, onReset, horizontal = false }) => {
  if (horizontal) {
    return (
      <Card>
        <CardContent>
          <Stack direction="row" spacing={2} alignItems="center" flexWrap="wrap">
            <Typography variant="h6" fontWeight={700} sx={{ minWidth: '60px' }}>
              Filters
            </Typography>
            <TextField
              size="small"
              placeholder="Search by name"
              value={filters.name || ''}
              onChange={(e) => onChange('name', e.target.value)}
              sx={{ minWidth: 200 }}
            />
            <FormControl size="small" sx={{ minWidth: 180 }}>
              <Select
                value={filters.specialization || ''}
                displayEmpty
                onChange={(e) => onChange('specialization', e.target.value)}
              >
                <MenuItem value="">All Specialties</MenuItem>
                {SPECIALIZATIONS.map((s) => (
                  <MenuItem key={s.value} value={s.value}>
                    {s.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <TextField
              size="small"
              placeholder="Location"
              value={filters.location || ''}
              onChange={(e) => onChange('location', e.target.value)}
              sx={{ minWidth: 150 }}
            />
            <TextField
              size="small"
              type="number"
              placeholder="Min Experience"
              inputProps={{ min: 0 }}
              value={filters.minExperience || ''}
              onChange={(e) => onChange('minExperience', e.target.value)}
              sx={{ minWidth: 140 }}
            />
            <TextField
              size="small"
              type="number"
              placeholder="Max Fee"
              inputProps={{ min: 0 }}
              value={filters.maxFee || ''}
              onChange={(e) => onChange('maxFee', e.target.value)}
              sx={{ minWidth: 120 }}
            />
            <Button size="small" onClick={onReset} variant="outlined">
              Reset
            </Button>
          </Stack>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent>
        <Stack spacing={3}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6" fontWeight={700}>
              Filters
            </Typography>
            <Button size="small" onClick={onReset}>
              Reset All
            </Button>
          </Box>

          <Divider />

          <Box>
            <Typography variant="subtitle2" fontWeight={600} gutterBottom>
              Doctor Name
            </Typography>
            <TextField
              fullWidth
              size="small"
              placeholder="Search by name"
              value={filters.name || ''}
              onChange={(e) => onChange('name', e.target.value)}
            />
          </Box>

          <Box>
            <Typography variant="subtitle2" fontWeight={600} gutterBottom>
              Specialization
            </Typography>
            <FormControl fullWidth size="small">
              <Select
                value={filters.specialization || ''}
                displayEmpty
                onChange={(e) => onChange('specialization', e.target.value)}
              >
                <MenuItem value="">All Specialties</MenuItem>
                {SPECIALIZATIONS.map((s) => (
                  <MenuItem key={s.value} value={s.value}>
                    {s.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          <Box>
            <Typography variant="subtitle2" fontWeight={600} gutterBottom>
              Location
            </Typography>
            <TextField
              fullWidth
              size="small"
              placeholder="City or area"
              value={filters.location || ''}
              onChange={(e) => onChange('location', e.target.value)}
            />
          </Box>

          <Box>
            <Typography variant="subtitle2" fontWeight={600} gutterBottom>
              Min. Experience (years)
            </Typography>
            <TextField
              fullWidth
              size="small"
              type="number"
              inputProps={{ min: 0 }}
              value={filters.minExperience || ''}
              onChange={(e) => onChange('minExperience', e.target.value)}
            />
          </Box>

          <Box>
            <Typography variant="subtitle2" fontWeight={600} gutterBottom>
              Max Consultation Fee
            </Typography>
            <TextField
              fullWidth
              size="small"
              type="number"
              inputProps={{ min: 0 }}
              value={filters.maxFee || ''}
              onChange={(e) => onChange('maxFee', e.target.value)}
            />
          </Box>

        </Stack>
      </CardContent>
    </Card>
  );
};

export default FilterPanel;
