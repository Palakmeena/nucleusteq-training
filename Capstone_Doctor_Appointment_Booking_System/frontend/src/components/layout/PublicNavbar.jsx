import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const PublicNavbar = () => {
  const location = useLocation();
  const { user, logout } = useAuth();
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const navLinks = [
    { path: '/doctors', label: 'Find Doctors' },
    { path: '/how-it-works', label: 'How it Works' },
    { path: '/specialties', label: 'Specialties' },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <header className={`pub-nav ${scrolled ? 'pub-nav--scrolled' : ''}`}>
      <div className="pub-nav-inner">
        {/* Brand */}
        <Link to="/" className="pub-nav-brand" id="navbar-home-link">
          <div className="pub-nav-brand-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
            </svg>
          </div>
          <span>MedPulse</span>
        </Link>

        {/* Nav links */}
        <nav className="pub-nav-links">
          {navLinks.map((link) => (
            <Link
              key={link.path}
              to={link.path}
              className={`pub-nav-link ${isActive(link.path) ? 'pub-nav-link--active' : ''}`}
            >
              {link.label}
            </Link>
          ))}
        </nav>

        {/* Actions */}
        <div className="pub-nav-actions">
          {user ? (
            <>
              <button className="pub-nav-btn pub-nav-btn--ghost" onClick={logout} id="navbar-logout-btn">
                Sign Out
              </button>
              <Link
                to={
                  user.role === 'ADMIN'
                    ? '/admin/dashboard'
                    : user.role === 'DOCTOR'
                    ? '/doctor/dashboard'
                    : '/dashboard'
                }
                className="pub-nav-btn pub-nav-btn--primary"
                id="navbar-dashboard-btn"
              >
                Dashboard
              </Link>
            </>
          ) : (
            <>
              <Link to="/login" className="pub-nav-btn pub-nav-btn--ghost" id="navbar-signin-btn">
                Sign In
              </Link>
              <Link to="/register" className="pub-nav-btn pub-nav-btn--primary" id="navbar-register-btn">
                Get Started
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default PublicNavbar;
