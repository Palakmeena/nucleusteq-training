import dayjs from 'dayjs';

export const formatDate = (date) => {
  if (!date) return '—';
  return dayjs(date).format('MMM D, YYYY');
};

export const formatTime = (time) => {
  if (!time) return '—';
  return dayjs(`2000-01-01 ${time}`).format('h:mm A');
};

export const formatCurrency = (amount) => {
  if (amount == null) return '—';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(amount);
};
