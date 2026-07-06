import React from 'react';
import { Outlet } from 'react-router-dom';
import PublicNavbar from './PublicNavbar';
import '../../styles/layout.css';

const PublicLayout = () => (
  <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh', background: '#f8fafc' }}>
    <PublicNavbar />
    <main style={{ flexGrow: 1 }}>
      <Outlet />
    </main>
  </div>
);

export default PublicLayout;
