import React from 'react';
import { useAuth } from '../../context/AuthContext';
import '../../styles/layout.css';

const DashboardNavbar = ({ onMenuClick, title }) => {
  const { user } = useAuth();
  const initials = (user?.fullName || user?.email || 'U')
    .split(' ')
    .map((n) => n[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();

  return (
    <header className="dash-navbar">
      {/* Hamburger (mobile) */}
      <button
        className="dash-navbar-menu-btn"
        onClick={onMenuClick}
        aria-label="Open sidebar menu"
        id="dashboard-menu-btn"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <line x1="3" y1="6" x2="21" y2="6" />
          <line x1="3" y1="12" x2="21" y2="12" />
          <line x1="3" y1="18" x2="21" y2="18" />
        </svg>
      </button>

      {/* Title */}
      <div className="dash-navbar-title">{title || 'Dashboard'}</div>

      {/* User info */}
      <div className="dash-navbar-user">
        <div className="dash-navbar-user-info">
          <div className="dash-navbar-user-name">
            {user?.fullName || user?.email || 'User'}
          </div>
          <div className="dash-navbar-user-role">{user?.role}</div>
        </div>
        <div className="dash-navbar-avatar" aria-label={`Logged in as ${user?.fullName || user?.email}`}>
          {initials}
        </div>
      </div>
    </header>
  );
};

export default DashboardNavbar;
