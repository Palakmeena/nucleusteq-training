import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { ROLES } from '../../constants/roles';
import '../../styles/layout.css';

const DRAWER_WIDTH = 260;

const MENU_CONFIG = {
  [ROLES.PATIENT]: [
    {
      text: 'Dashboard',
      path: '/dashboard',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="3" width="7" height="7" /><rect x="14" y="3" width="7" height="7" />
          <rect x="14" y="14" width="7" height="7" /><rect x="3" y="14" width="7" height="7" />
        </svg>
      ),
    },
    {
      text: 'Find Doctors',
      path: '/find-doctors',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
        </svg>
      ),
    },
    {
      text: 'My Appointments',
      path: '/appointments',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
          <line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" />
          <line x1="3" y1="10" x2="21" y2="10" />
        </svg>
      ),
    },
  ],
  [ROLES.DOCTOR]: [
    {
      text: 'Dashboard',
      path: '/doctor/dashboard',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="3" width="7" height="7" /><rect x="14" y="3" width="7" height="7" />
          <rect x="14" y="14" width="7" height="7" /><rect x="3" y="14" width="7" height="7" />
        </svg>
      ),
    },
    {
      text: 'Appointments',
      path: '/doctor/appointments',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
          <line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" />
          <line x1="3" y1="10" x2="21" y2="10" />
        </svg>
      ),
    },
    {
      text: 'Manage Slots',
      path: '/doctor/slots',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="12" cy="12" r="10" />
          <polyline points="12 6 12 12 16 14" />
        </svg>
      ),
    },
    {
      text: 'Profile',
      path: '/doctor/profile',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
          <circle cx="12" cy="7" r="4" />
        </svg>
      ),
    },
  ],
  [ROLES.ADMIN]: [
    {
      text: 'Dashboard',
      path: '/admin/dashboard',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="3" width="7" height="7" /><rect x="14" y="3" width="7" height="7" />
          <rect x="14" y="14" width="7" height="7" /><rect x="3" y="14" width="7" height="7" />
        </svg>
      ),
    },
    {
      text: 'Doctors',
      path: '/admin/doctors',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
        </svg>
      ),
    },
    {
      text: 'Patients',
      path: '/admin/patients',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
          <path d="M16 3.13a4 4 0 0 1 0 7.75" />
        </svg>
      ),
    },
    {
      text: 'Appointments',
      path: '/admin/appointments',
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
          <line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" />
          <line x1="3" y1="10" x2="21" y2="10" />
        </svg>
      ),
    },
  ],
};

const PORTAL_LABELS = {
  [ROLES.PATIENT]: 'Patient Portal',
  [ROLES.DOCTOR]: 'Doctor Portal',
  [ROLES.ADMIN]: 'Admin Portal',
};

const Sidebar = ({ mobileOpen, onMobileClose, role }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { logout } = useAuth();
  const menuItems = MENU_CONFIG[role] || MENU_CONFIG[ROLES.PATIENT];

  const handleLogout = () => {
    logout();
    navigate('/login');
    onMobileClose?.();
  };

  return (
    <>
      {/* Overlay for mobile */}
      {mobileOpen && (
        <div
          style={{
            position: 'fixed', inset: 0, zIndex: 49,
            background: 'rgba(15,23,42,0.35)', backdropFilter: 'blur(2px)',
          }}
          onClick={onMobileClose}
        />
      )}

      <aside
        className={`sidebar-root${mobileOpen ? ' sidebar-root--open' : ''}`}
        style={{ width: DRAWER_WIDTH }}
      >
        {/* Brand */}
        <div className="sidebar-brand">
          <div className="sidebar-brand-row">
            <div className="sidebar-brand-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
              </svg>
            </div>
            <span className="sidebar-brand-name">MedPulse</span>
          </div>
          <span className="sidebar-portal-label">{PORTAL_LABELS[role] || 'Portal'}</span>
        </div>

        <div className="sidebar-divider" />

        {/* Navigation */}
        <nav className="sidebar-nav">
          {menuItems.map((item) => {
            const active = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`sidebar-nav-item${active ? ' sidebar-nav-item--active' : ''}`}
                onClick={onMobileClose}
              >
                <span className="sidebar-nav-icon">{item.icon}</span>
                {item.text}
              </Link>
            );
          })}
        </nav>

        {/* Footer / Logout */}
        <div className="sidebar-footer">
          <button className="sidebar-logout-btn" onClick={handleLogout} id="sidebar-logout-btn">
            <span className="sidebar-logout-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                <polyline points="16 17 21 12 16 7" />
                <line x1="21" y1="12" x2="9" y2="12" />
              </svg>
            </span>
            Sign Out
          </button>
        </div>
      </aside>
    </>
  );
};

export { DRAWER_WIDTH };
export default Sidebar;
