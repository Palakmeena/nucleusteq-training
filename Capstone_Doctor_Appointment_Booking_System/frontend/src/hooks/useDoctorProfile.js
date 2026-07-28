import { useState, useEffect } from 'react';
import doctorApi from '../api/doctorApi';
import { useAuth } from '../context/AuthContext';

export const useDoctorProfileId = () => {
  const { user } = useAuth();
  const [doctorId, setDoctorId] = useState(
    () => localStorage.getItem('doctorProfileId') || null
  );
  const [loading, setLoading] = useState(!doctorId);

  useEffect(() => {
    if (!user || user.role !== 'DOCTOR' || doctorId) {
      setLoading(false);
      return;
    }

    const resolveDoctorId = async () => {
      try {
        const response = await doctorApi.getDoctors({
          name: user.fullName?.split(' ')[0],
        });
        const match = (response.data || []).find(
          (d) => d.full_name === user.fullName
        );
        if (match?.id) {
          localStorage.setItem('doctorProfileId', match.id);
          setDoctorId(match.id);
        }
      } catch {
        // silently fail — slots page will show empty state
      } finally {
        setLoading(false);
      }
    };

    resolveDoctorId();
  }, [user, doctorId]);

  return { doctorId, loading };
};
