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
      if (filters.location) params.location = filters.location;
      if (filters.minExperience) params.min_experience = Number(filters.minExperience);
      if (filters.maxFee) params.max_fee = Number(filters.maxFee);

      const response = await doctorApi.getDoctors(params);
      let results = response.data || [];

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
