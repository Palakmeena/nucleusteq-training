import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
    <nav className="navbar">
      <div className="container navbar-container">
        <Link to="/" className="navbar-logo">
          MedPulse
        </Link>
        <ul className="navbar-links">
          <li><Link to="/doctors">Find Doctors</Link></li>
          <li><Link to="/how-it-works">How it Works</Link></li>
          <li><Link to="/specialties">Specialties</Link></li>
        </ul>
        <div className="navbar-actions">
          <Link to="/login" className="btn btn-outline nav-login-btn">Sign In</Link>
          <Link to="/register" className="btn btn-primary">Register</Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
