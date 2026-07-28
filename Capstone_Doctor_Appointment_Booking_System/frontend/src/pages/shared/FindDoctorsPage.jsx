import React from 'react';
import { Box, Grid } from '@mui/material';
import PageHeader from '../../components/common/PageHeader';
import FilterPanel from '../../components/common/FilterPanel';
import DoctorCard from '../../components/cards/DoctorCard';
import EmptyState from '../../components/emptyState/EmptyState';
import { ListSkeleton } from '../../components/loading/SkeletonLoader';
import { useDoctors } from '../../hooks/useDoctors';
import { MedicalServices as MedicalIcon } from '@mui/icons-material';

const DEFAULT_FILTERS = {
  name: '',
  specialization: '',
  location: '',
  minExperience: '',
  maxFee: '',
};

const FindDoctorsPage = () => {
  const { doctors, loading, filters, setFilters } = useDoctors(DEFAULT_FILTERS);

  const handleFilterChange = (field, value) => {
    setFilters((prev) => ({ ...prev, [field]: value }));
  };

  const resetFilters = () => setFilters(DEFAULT_FILTERS);

  return (
    <Box>
      <PageHeader
        title="Find Doctors"
        subtitle="Search verified specialists and book appointments online"
      />

      <Box sx={{ mb: 3 }}>
        <FilterPanel filters={filters} onChange={handleFilterChange} onReset={resetFilters} horizontal />
      </Box>

      {loading ? (
        <ListSkeleton count={4} />
      ) : doctors.length > 0 ? (
        <Grid container spacing={3}>
          {doctors.map((doctor) => (
            <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }} key={doctor.id}>
              <DoctorCard doctor={doctor} />
            </Grid>
          ))}
        </Grid>
      ) : (
        <EmptyState
          icon={MedicalIcon}
          title="No doctors found"
          description="Try adjusting your search or filters"
        />
      )}
    </Box>
  );
};

export default FindDoctorsPage;
