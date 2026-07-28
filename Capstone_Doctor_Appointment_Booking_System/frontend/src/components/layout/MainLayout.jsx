import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import DashboardNavbar from './DashboardNavbar';
import { useAuth } from '../../context/AuthContext';
import '../../styles/layout.css';

const MainLayout = () => {
  const { user } = useAuth();
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="dash-layout">
      <Sidebar
        role={user?.role}
        mobileOpen={mobileOpen}
        onMobileClose={() => setMobileOpen(false)}
      />
      <div className="dash-main">
        <DashboardNavbar onMenuClick={() => setMobileOpen(true)} />
        <div className="dash-content">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default MainLayout;
