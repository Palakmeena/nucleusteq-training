import React from 'react';
import { Link } from 'react-router-dom';
import './HomePage.css';

const HomePage = () => {
  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="container hero-container">
          <div className="hero-content">
            <div className="trust-badge">
              <span className="badge-icon">✓</span> Trusted by 2M+ Patients
            </div>
            <h1 className="hero-title">
              Your health,<br />
              <span className="highlight-text">simplified.</span>
            </h1>
            <p className="hero-subtitle">
              Find and book the highest rated doctors in your city. Get care immediately with 24/7 access to online consultations or in-person visits.
            </p>
            
            <div className="search-bar-widget">
              <div className="search-input-group">
                <span className="search-icon">🔍</span>
                <input type="text" placeholder="Specialty or ID" />
              </div>
              <div className="search-input-group">
                <span className="search-icon">📍</span>
                <input type="text" placeholder="Location" />
              </div>
              <button className="btn btn-primary search-btn">Find Doctor</button>
            </div>
            
            <div className="social-proof">
              <div className="avatars">
                {/* Placeholders for avatars */}
                <div className="avatar"></div>
                <div className="avatar"></div>
                <div className="avatar"></div>
              </div>
              <p>Joined by 10,000+ specialists this month</p>
            </div>
          </div>
          
          <div className="hero-image-wrapper">
            {/* Placeholder for the main image */}
            <div className="hero-image-placeholder">
              <div className="image-card float-card-1">
                <span className="icon">📅</span>
                <div className="card-text">
                  <strong>Continuing</strong>
                  <span>Average appointment scheduled within 15 hours</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="features-section">
        <div className="container">
          <div className="section-header text-center">
            <span className="section-subtitle">FEATURES</span>
            <h2 className="section-title">Designed for your well-being</h2>
          </div>
          
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">📅</div>
              <h3>Instant Online Booking</h3>
              <p>Skip the waiting room. See real-time availability and book your next appointment in under 60 seconds. Sync directly with your personal calendar.</p>
            </div>
            <div className="feature-card feature-card-primary">
              <div className="feature-icon text-white">📞</div>
              <h3 className="text-white">24/7 Virtual Access</h3>
              <p className="text-white">Talk to a general practitioner anytime, anywhere via high-definition video call for urgent non-emergencies.</p>
              <button className="btn btn-white mt-4">Start Now</button>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🛡️</div>
              <h3>Verified Doctors Only</h3>
              <p>Every professional undergoes a multi-stage background check and credential verification process.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🔒</div>
              <h3>HIPAA Compliant</h3>
              <p>Your data is encrypted and stored according to the highest medical security standards. Your privacy is our priority.</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;
