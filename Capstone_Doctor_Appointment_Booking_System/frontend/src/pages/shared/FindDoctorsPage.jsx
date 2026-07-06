import React, { useMemo } from 'react';
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
  availability: '',
};

const FindDoctorsPage = () => {
  const { doctors, loading, filters, setFilters } = useDoctors(DEFAULT_FILTERS);

  const handleFilterChange = (field, value) => {
    setFilters((prev) => ({ ...prev, [field]: value }));
  };

  const resetFilters = () => setFilters(DEFAULT_FILTERS);

  const filteredDoctors = useMemo(() => {
    if (!filters.availability) return doctors;
    // Availability is UI-only until backend supports it
    return doctors;
  }, [doctors, filters.availability]);

  return (
    <Box>
      <PageHeader
        title="Find Doctors"
        subtitle="Search verified specialists and book appointments online"
      />

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 3 }}>
          <FilterPanel filters={filters} onChange={handleFilterChange} onReset={resetFilters} />
        </Grid>

        <Grid size={{ xs: 12, md: 9 }}>
          {loading ? (
            <ListSkeleton count={4} />
          ) : filteredDoctors.length > 0 ? (
            <Grid container spacing={3}>
              {filteredDoctors.map((doctor) => (
                <Grid size={{ xs: 12, sm: 6, lg: 4 }} key={doctor.id}>
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
        </Grid>
      </Grid>
    </Box>
  );
};

export default FindDoctorsPage;
