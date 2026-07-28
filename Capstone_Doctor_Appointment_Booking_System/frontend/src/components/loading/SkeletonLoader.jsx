import React from 'react';
import { Box, Skeleton, Grid } from '@mui/material';

export const DoctorCardSkeleton = () => (
  <Box sx={{ p: 2 }}>
    <StackSkeleton rows={4} />
  </Box>
);

const StackSkeleton = ({ rows = 3 }) => (
  <Box>
    {Array.from({ length: rows }).map((_, i) => (
      <Skeleton key={i} variant="rounded" height={48} sx={{ mb: 1.5 }} />
    ))}
  </Box>
);

export const DashboardSkeleton = () => (
  <Grid container spacing={3}>
    {[1, 2, 3, 4].map((i) => (
      <Grid item xs={12} sm={6} md={3} key={i}>
        <Skeleton variant="rounded" height={100} />
      </Grid>
    ))}
    <Grid item xs={12}>
      <Skeleton variant="rounded" height={300} />
    </Grid>
  </Grid>
);

export const ListSkeleton = ({ count = 3 }) => (
  <Box>
    {Array.from({ length: count }).map((_, i) => (
      <Skeleton key={i} variant="rounded" height={88} sx={{ mb: 2 }} />
    ))}
  </Box>
);

export default { DoctorCardSkeleton, DashboardSkeleton, ListSkeleton };
