import { useState, useEffect, useCallback } from 'react';
import doctorApi from '../api/doctorApi';
import { showError } from '../utils/errorHandler';

export const useDoctors = (initialFilters = {}) => {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState(initialFilters);

  const fetchDoctors = useCallback(async () => {
    try {
      setLoading(true);
      const params = {};
      if (filters.name) params.name = filters.name;
      if (filters.specialization) params.specialization = filters.specialization;

      const response = await doctorApi.getDoctors(params);
      let results = response.data || [];

      if (filters.location) {
        const loc = filters.location.toLowerCase();
        results = results.filter((d) =>
          d.clinic_address?.toLowerCase().includes(loc)
        );
      }

      if (filters.minExperience) {
        results = results.filter((d) => d.experience >= Number(filters.minExperience));
      }

      if (filters.maxFee) {
        results = results.filter((d) => d.consultation_fee <= Number(filters.maxFee));
      }

      setDoctors(results);
    } catch (error) {
      showError(error, 'Failed to load doctors');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchDoctors();
  }, [fetchDoctors]);

  return { doctors, loading, filters, setFilters, refetch: fetchDoctors };
};
