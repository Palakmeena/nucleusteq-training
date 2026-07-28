export const ROLES = {
  PATIENT: 'PATIENT',
  DOCTOR: 'DOCTOR',
  ADMIN: 'ADMIN',
};

export const DASHBOARD_PATHS = {
  [ROLES.PATIENT]: '/dashboard',
  [ROLES.DOCTOR]: '/doctor/dashboard',
  [ROLES.ADMIN]: '/admin/dashboard',
};

export const getDashboardPath = (role) => DASHBOARD_PATHS[role] || '/dashboard';
