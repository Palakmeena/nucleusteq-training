import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import 'react-toastify/dist/ReactToastify.css';
import './index.css';

// Layouts
import PublicLayout from './components/layout/PublicLayout';
import MainLayout from './components/layout/MainLayout';

// Route Guards
import ProtectedRoute from './routes/ProtectedRoute';

// Constants
import { ROLES } from './constants/roles';

// Public Pages
import HomePage from './pages/shared/HomePage';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';

// Patient Pages
import PatientDashboard from './pages/patient/PatientDashboard';
import PatientAppointmentsPage from './pages/patient/PatientAppointmentsPage';
import AppointmentSuccessPage from './pages/patient/AppointmentSuccessPage';
import PaymentPage from './pages/patient/PaymentPage';

// Doctor Pages
import DoctorDashboard from './pages/doctor/DoctorDashboard';
import DoctorAppointmentsPage from './pages/doctor/DoctorAppointmentsPage';
import DoctorProfilePage from './pages/doctor/DoctorProfilePage';
import DoctorSlotsPage from './pages/doctor/DoctorSlotsPage';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminDoctorsPage from './pages/admin/AdminDoctorsPage';
import AdminPatientsPage from './pages/admin/AdminPatientsPage';
import AdminAppointmentsPage from './pages/admin/AdminAppointmentsPage';

// Shared Pages
import FindDoctorsPage from './pages/shared/FindDoctorsPage';
import SharedDoctorProfilePage from './pages/shared/DoctorProfilePage';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2563eb',
      dark: '#1d4ed8',
      light: '#dbeafe',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#f1f5f9',
      dark: '#e2e8f0',
      contrastText: '#0f172a',
    },
    success: {
      main: '#16a34a',
      light: '#dcfce7',
    },
    error: {
      main: '#dc2626',
      light: '#fee2e2',
    },
    warning: {
      main: '#d97706',
      light: '#fef3c7',
    },
    info: {
      main: '#0891b2',
      light: '#cffafe',
    },
    background: {
      default: '#f8fafc',
      paper: '#ffffff',
    },
    text: {
      primary: '#0f172a',
      secondary: '#475569',
    },
    divider: '#e2e8f0',
  },
  typography: {
    fontFamily: '"Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    h1: { fontWeight: 800, letterSpacing: '-0.02em' },
    h2: { fontWeight: 700, letterSpacing: '-0.02em' },
    h3: { fontWeight: 700, letterSpacing: '-0.01em' },
    h4: { fontWeight: 700, letterSpacing: '-0.01em' },
    h5: { fontWeight: 600 },
    h6: { fontWeight: 600 },
    button: { fontWeight: 600, textTransform: 'none' },
  },
  shape: {
    borderRadius: 10,
  },
  shadows: [
    'none',
    '0 1px 3px rgba(0,0,0,0.06)',
    '0 2px 6px rgba(0,0,0,0.08)',
    '0 4px 12px rgba(0,0,0,0.08)',
    '0 6px 16px rgba(0,0,0,0.09)',
    '0 8px 24px rgba(0,0,0,0.10)',
    '0 10px 30px rgba(0,0,0,0.10)',
    '0 12px 36px rgba(0,0,0,0.11)',
    '0 14px 42px rgba(0,0,0,0.11)',
    '0 16px 48px rgba(0,0,0,0.12)',
    '0 18px 54px rgba(0,0,0,0.12)',
    '0 20px 60px rgba(0,0,0,0.13)',
    '0 22px 66px rgba(0,0,0,0.13)',
    '0 24px 72px rgba(0,0,0,0.14)',
    '0 26px 78px rgba(0,0,0,0.14)',
    '0 28px 84px rgba(0,0,0,0.15)',
    '0 30px 90px rgba(0,0,0,0.15)',
    '0 32px 96px rgba(0,0,0,0.16)',
    '0 34px 100px rgba(0,0,0,0.16)',
    '0 36px 106px rgba(0,0,0,0.17)',
    '0 38px 112px rgba(0,0,0,0.17)',
    '0 40px 118px rgba(0,0,0,0.18)',
    '0 42px 124px rgba(0,0,0,0.18)',
    '0 44px 130px rgba(0,0,0,0.19)',
    '0 46px 136px rgba(0,0,0,0.20)',
  ],
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          textTransform: 'none',
          fontWeight: 600,
          fontSize: '0.875rem',
          padding: '8px 20px',
        },
        contained: {
          boxShadow: '0 2px 8px rgba(37,99,235,0.25)',
          '&:hover': {
            boxShadow: '0 4px 14px rgba(37,99,235,0.35)',
          },
        },
        sizeLarge: {
          padding: '12px 28px',
          fontSize: '0.95rem',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 1px 4px rgba(0,0,0,0.06), 0 2px 8px rgba(0,0,0,0.04)',
          border: '1px solid #e2e8f0',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 12,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
            backgroundColor: '#f8fafc',
            '&:hover .MuiOutlinedInput-notchedOutline': {
              borderColor: '#2563eb',
            },
            '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
              borderColor: '#2563eb',
              borderWidth: 2,
            },
          },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 6,
          fontWeight: 600,
          fontSize: '0.75rem',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          boxShadow: 'none',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          borderRight: '1px solid #e2e8f0',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
      },
    },
    MuiAvatar: {
      styleOverrides: {
        root: {
          fontWeight: 700,
        },
      },
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />

      <Router>
        <Routes>

          {/* ================= PUBLIC ROUTES ================= */}

          <Route path="/" element={<PublicLayout />}>
            <Route index element={<HomePage />} />
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
          </Route>

          {/* ================= PATIENT ROUTES ================= */}

          <Route
            element={
              <ProtectedRoute allowedRoles={[ROLES.PATIENT]}>
                <MainLayout />
              </ProtectedRoute>
            }
          >
            <Route path="/dashboard" element={<PatientDashboard />} />
            <Route path="/find-doctors" element={<FindDoctorsPage />} />
            <Route path="/doctor/:id" element={<SharedDoctorProfilePage />} />
            <Route path="/appointments" element={<PatientAppointmentsPage />} />
            <Route path="/appointments/:id/payment" element={<PaymentPage />} />
            <Route path="/payment" element={<PaymentPage />} />
            <Route
              path="/appointment-success"
              element={<AppointmentSuccessPage />}
            />
          </Route>

          {/* ================= DOCTOR ROUTES ================= */}

          <Route
            element={
              <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
                <MainLayout />
              </ProtectedRoute>
            }
          >
            <Route
              path="/doctor/dashboard"
              element={<DoctorDashboard />}
            />

            <Route
              path="/doctor/profile"
              element={<DoctorProfilePage />}
            />

            <Route
              path="/doctor/appointments"
              element={<DoctorAppointmentsPage />}
            />

            <Route
              path="/doctor/slots"
              element={<DoctorSlotsPage />}
            />
          </Route>

          {/* ================= ADMIN ROUTES ================= */}

          <Route
            element={
              <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
                <MainLayout />
              </ProtectedRoute>
            }
          >
            <Route
              path="/admin/dashboard"
              element={<AdminDashboard />}
            />

            <Route
              path="/admin/doctors"
              element={<AdminDoctorsPage />}
            />

            <Route
              path="/admin/patients"
              element={<AdminPatientsPage />}
            />

            <Route
              path="/admin/appointments"
              element={<AdminAppointmentsPage />}
            />
          </Route>

          {/* ================= FALLBACK ================= */}

          <Route path="*" element={<Navigate to="/" replace />} />

        </Routes>

        <ToastContainer
          position="bottom-right"
          autoClose={3000}
        />
      </Router>
    </ThemeProvider>
  );
}

export default App;