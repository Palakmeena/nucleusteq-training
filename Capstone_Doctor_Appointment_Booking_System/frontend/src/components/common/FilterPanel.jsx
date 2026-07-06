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
  RadioGroup,
  FormControlLabel,
  Radio,
  Checkbox,
  Button,
  Stack,
  Divider,
} from '@mui/material';
import { Star as StarIcon } from '@mui/icons-material';
import { SPECIALIZATIONS } from '../../constants/specializations';

const FilterPanel = ({ filters, onChange, onReset }) => (
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
                <MenuItem key={s} value={s}>
                  {s}
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

        <Box>
          <Typography variant="subtitle2" fontWeight={600} gutterBottom>
            Availability
          </Typography>
          <FormControl component="fieldset">
            <RadioGroup
              value={filters.availability || ''}
              onChange={(e) => onChange('availability', e.target.value)}
            >
              <FormControlLabel value="today" control={<Radio size="small" />} label="Today" />
              <FormControlLabel value="tomorrow" control={<Radio size="small" />} label="Tomorrow" />
              <FormControlLabel value="week" control={<Radio size="small" />} label="This Week" />
            </RadioGroup>
          </FormControl>
        </Box>
      </Stack>
    </CardContent>
  </Card>
);

export default FilterPanel;
