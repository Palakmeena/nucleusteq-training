import { toast } from 'react-toastify';

export const getErrorMessage = (error, fallback = 'Something went wrong') => {
  const detail = error?.response?.data?.detail;

  if (!detail) {
    if (!error?.response) return 'Network error. Please check your connection.';
    return fallback;
  }

  if (typeof detail === 'string') return detail;

  if (Array.isArray(detail)) {
    return detail.map((d) => d.msg || d.message || String(d)).join(', ');
  }

  return fallback;
};

export const showError = (error, fallback) => {
  toast.error(getErrorMessage(error, fallback));
};
