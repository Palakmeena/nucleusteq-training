import React from 'react';
import { Link } from 'react-router-dom';
import Footer from '../../components/layout/Footer';
import './HomePage.css';

const specialties = [
  {
    name: 'Cardiology',
    desc: 'Heart & vascular care',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
      </svg>
    ),
    color: '#ef4444',
    bg: '#fef2f2',
  },
  {
    name: 'Pediatrics',
    desc: 'Child health specialist',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="8" r="4" />
        <path d="M6 20v-2a4 4 0 0 1 4-4h4a4 4 0 0 1 4 4v2" />
      </svg>
    ),
    color: '#16a34a',
    bg: '#f0fdf4',
  },
  {
    name: 'Neurology',
    desc: 'Brain & nervous system',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M9.5 2A2.5 2.5 0 0 1 12 4.5v15a2.5 2.5 0 0 1-4.96-.46 2.5 2.5 0 0 1-2.96-3.08 3 3 0 0 1-.34-5.58 2.5 2.5 0 0 1 1.32-4.24 2.5 2.5 0 0 1 4.44-2.14" />
        <path d="M14.5 2A2.5 2.5 0 0 0 12 4.5v15a2.5 2.5 0 0 0 4.96-.46 2.5 2.5 0 0 0 2.96-3.08 3 3 0 0 0 .34-5.58 2.5 2.5 0 0 0-1.32-4.24 2.5 2.5 0 0 0-4.44-2.14" />
      </svg>
    ),
    color: '#2563eb',
    bg: '#eff6ff',
  },
  {
    name: 'Dentistry',
    desc: 'Oral & dental health',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 5.5c-1.5-2-4-2-5.5-.5C5 6.5 5 9 6 11l3 7c.5 1 1.5 1 2 0s1.5-1 2 0l3-7c1-2 1-4.5-.5-6C14 3.5 13.5 3.5 12 5.5z" />
      </svg>
    ),
    color: '#d97706',
    bg: '#fffbeb',
  },
  {
    name: 'Dermatology',
    desc: 'Skin & hair treatment',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M14 12a2 2 0 0 1-2 2a2 2 0 0 1-2-2c0-1.1 2-6 2-6s2 4.9 2 6z" />
        <path d="M6 20V4a2 2 0 0 1 4 0v5" />
        <path d="M14 9V4a2 2 0 0 1 4 0v11a6 6 0 0 1-6 6H8a6 6 0 0 1-6-6V9" />
      </svg>
    ),
    color: '#0891b2',
    bg: '#ecfeff',
  },
  {
    name: 'Orthopedics',
    desc: 'Bone & joint care',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M18 8h1a4 4 0 0 1 0 8h-1" />
        <path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z" />
        <line x1="6" y1="1" x2="6" y2="4" />
        <line x1="10" y1="1" x2="10" y2="4" />
        <line x1="14" y1="1" x2="14" y2="4" />
      </svg>
    ),
    color: '#7c3aed',
    bg: '#f5f3ff',
  },
];

const features = [
  {
    title: 'Instant Online Booking',
    desc: 'See real-time availability and book your next appointment in under 60 seconds.',
    icon: (
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
        <line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" />
        <line x1="3" y1="10" x2="21" y2="10" />
      </svg>
    ),
    color: '#2563eb',
    bg: '#eff6ff',
  },
  {
    title: '24/7 Virtual Access',
    desc: 'Talk to a general practitioner anytime via high-definition video call.',
    icon: (
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <polygon points="23 7 16 12 23 17 23 7" />
        <rect x="1" y="5" width="15" height="14" rx="2" ry="2" />
      </svg>
    ),
    color: '#7c3aed',
    bg: '#f5f3ff',
    featured: true,
  },
  {
    title: 'Verified Doctors Only',
    desc: 'Every professional undergoes a multi-stage background check and credential verification.',
    icon: (
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
      </svg>
    ),
    color: '#16a34a',
    bg: '#f0fdf4',
  },
  {
    title: 'HIPAA Compliant',
    desc: 'Your data is encrypted and stored according to the highest medical security standards.',
    icon: (
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
        <path d="M7 11V7a5 5 0 0 1 10 0v4" />
      </svg>
    ),
    color: '#0891b2',
    bg: '#ecfeff',
  },
];

const steps = [
  {
    num: '01',
    title: 'Search for a Specialist',
    desc: 'Browse our extensive network of verified doctors by specialty, location, or availability.',
  },
  {
    num: '02',
    title: 'Choose your Time',
    desc: 'Select from available time slots that fit your schedule with real-time availability.',
  },
  {
    num: '03',
    title: 'Attend your Visit',
    desc: 'Connect with your doctor in-person or via video call from the comfort of your home.',
  },
];

const testimonials = [
  {
    name: 'Sarah Johnson',
    role: 'Marketing Manager',
    text: 'MedPulse made it so easy to find a specialist. The booking process was seamless and I got an appointment within 24 hours.',
    initials: 'SJ',
    color: '#2563eb',
  },
  {
    name: 'Michael Chen',
    role: 'Software Engineer',
    text: 'The virtual consultation feature is a game-changer. I could consult with my doctor from the comfort of my home.',
    initials: 'MC',
    color: '#7c3aed',
  },
  {
    name: 'Emily Rodriguez',
    role: 'Teacher',
    text: 'Finally, a healthcare platform that actually works! The doctors are verified and the interface is incredibly user-friendly.',
    initials: 'ER',
    color: '#0891b2',
  },
];

const HomePage = () => {
  return (
    <div className="home">
      {/* ── HERO ── */}
      <section className="hero">
        <div className="hero-inner">
          <div className="hero-badge">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <polyline points="20 6 9 17 4 12" />
            </svg>
            Trusted by 2M+ Patients
          </div>

          <h1 className="hero-heading">
            Your health,{' '}
            <span className="hero-heading-accent">simplified.</span>
          </h1>

          <p className="hero-subheading">
            Find and book the highest rated doctors in your city. Get care immediately with 24/7 access to online consultations or in-person visits.
          </p>

          {/* Search bar */}
          <div className="hero-search">
            <div className="hero-search-field">
              <span className="hero-search-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" />
                </svg>
              </span>
              <input type="text" placeholder="Specialty or Doctor" className="hero-search-input" aria-label="Search specialty or doctor" />
            </div>
            <div className="hero-search-divider" />
            <div className="hero-search-field">
              <span className="hero-search-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                  <circle cx="12" cy="10" r="3" />
                </svg>
              </span>
              <input type="text" placeholder="City or Location" className="hero-search-input" aria-label="Search city or location" />
            </div>
            <Link to="/doctors" className="hero-search-btn" id="hero-find-doctor-btn">
              Find Doctor
            </Link>
          </div>

          {/* Social proof */}
          <div className="hero-proof">
            <div className="hero-avatars">
              <div className="hero-avatar" style={{ background: '#60a5fa' }}>S</div>
              <div className="hero-avatar" style={{ background: '#34d399' }}>M</div>
              <div className="hero-avatar" style={{ background: '#f472b6' }}>E</div>
              <div className="hero-avatar" style={{ background: '#a78bfa' }}>R</div>
            </div>
            <span>Joined by <strong>10,000+</strong> specialists this month</span>
          </div>
        </div>

        {/* Decorative blobs */}
        <div className="hero-blob hero-blob-1" />
        <div className="hero-blob hero-blob-2" />
      </section>

      {/* ── FEATURES ── */}
      <section className="section section--gray">
        <div className="section-inner">
          <div className="section-header">
            <span className="section-tag">Features</span>
            <h2 className="section-title">Designed for your well-being</h2>
            <p className="section-subtitle">Everything you need to manage your health in one seamless platform</p>
          </div>

          <div className="features-grid">
            {features.map((f) => (
              <div
                key={f.title}
                className={`feature-card ${f.featured ? 'feature-card--featured' : ''}`}
              >
                <div
                  className="feature-card-icon"
                  style={f.featured ? {} : { background: f.bg, color: f.color }}
                >
                  {f.icon}
                </div>
                <h3 className="feature-card-title">{f.title}</h3>
                <p className="feature-card-desc">{f.desc}</p>
                {f.featured && (
                  <Link to="/doctors" className="feature-card-btn" id={`feature-${f.title.replace(/\s/g, '-').toLowerCase()}`}>
                    Start Now
                  </Link>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA BANNER ── */}
      <section className="cta-section">
        <div className="cta-inner">
          <div className="cta-blob cta-blob-1" />
          <div className="cta-blob cta-blob-2" />
          <div className="cta-content">
            <h2>Ready to find your doctor?</h2>
            <p>Join thousands of patients who trust MedPulse for their healthcare needs. Book appointments in seconds with verified doctors.</p>
            <div className="cta-buttons">
              <Link to="/register" className="cta-btn cta-btn--primary" id="cta-get-started-btn">
                Get Started for Free
              </Link>
              <Link to="/doctors" className="cta-btn cta-btn--outline" id="cta-find-doctors-btn">
                Browse Doctors
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* ── SPECIALTIES ── */}
      <section className="section section--white">
        <div className="section-inner">
          <div className="section-header">
            <span className="section-tag">Specialties</span>
            <h2 className="section-title">Comprehensive care for every need</h2>
            <p className="section-subtitle">Browse our extensive network of verified specialists across all medical fields</p>
          </div>

          <div className="specialties-grid">
            {specialties.map((s) => (
              <Link to="/doctors" key={s.name} className="specialty-card" id={`specialty-${s.name.toLowerCase()}`}>
                <div className="specialty-icon" style={{ background: s.bg, color: s.color }}>
                  {s.icon}
                </div>
                <h3 className="specialty-name">{s.name}</h3>
                <p className="specialty-desc">{s.desc}</p>
              </Link>
            ))}
          </div>

          <div className="section-cta">
            <Link to="/doctors" className="section-cta-btn" id="view-all-specialties-btn">
              View All Specialties
            </Link>
          </div>
        </div>
      </section>

      {/* ── HOW IT WORKS ── */}
      <section className="section section--gray">
        <div className="section-inner">
          <div className="how-it-works">
            <div className="how-left">
              <span className="section-tag">How it Works</span>
              <h2 className="section-title" style={{ textAlign: 'left', marginBottom: '2.5rem' }}>
                Book an appointment in 3 simple steps
              </h2>

              <div className="steps">
                {steps.map((step, i) => (
                  <div className="step" key={step.num}>
                    <div className="step-num">{step.num}</div>
                    <div className="step-content">
                      <h3>{step.title}</h3>
                      <p>{step.desc}</p>
                    </div>
                    {i < steps.length - 1 && <div className="step-line" />}
                  </div>
                ))}
              </div>

              <Link to="/register" className="how-cta-btn" id="how-get-started-btn">
                Get Started Today
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                  <line x1="5" y1="12" x2="19" y2="12" /><polyline points="12 5 19 12 12 19" />
                </svg>
              </Link>
            </div>

            <div className="how-right">
              <div className="how-stats-card">
                <div className="how-stats-title">Platform Overview</div>
                <div className="how-stats-grid">
                  <div className="how-stat">
                    <span className="how-stat-value">2M+</span>
                    <span className="how-stat-label">Patients Served</span>
                  </div>
                  <div className="how-stat">
                    <span className="how-stat-value">10K+</span>
                    <span className="how-stat-label">Verified Doctors</span>
                  </div>
                  <div className="how-stat">
                    <span className="how-stat-value">50+</span>
                    <span className="how-stat-label">Specialties</span>
                  </div>
                  <div className="how-stat">
                    <span className="how-stat-value">4.9</span>
                    <span className="how-stat-label">Average Rating</span>
                  </div>
                </div>
                <div className="how-rating">
                  <div className="how-stars">
                    {[1,2,3,4,5].map(i => (
                      <svg key={i} width="16" height="16" viewBox="0 0 24 24" fill="#fbbf24" stroke="none">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
                      </svg>
                    ))}
                  </div>
                  <span>Rated 4.9/5 by 50,000+ patients</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ── TESTIMONIALS ── */}
      <section className="section section--white">
        <div className="section-inner">
          <div className="section-header">
            <span className="section-tag">Testimonials</span>
            <h2 className="section-title">Loved by patients everywhere</h2>
            <p className="section-subtitle">See what thousands of patients are saying about MedPulse</p>
          </div>

          <div className="testimonials-grid">
            {testimonials.map((t) => (
              <div className="testimonial-card" key={t.name}>
                <div className="testimonial-stars">
                  {[1,2,3,4,5].map(i => (
                    <svg key={i} width="14" height="14" viewBox="0 0 24 24" fill="#fbbf24" stroke="none">
                      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
                    </svg>
                  ))}
                </div>
                <p className="testimonial-text">"{t.text}"</p>
                <div className="testimonial-author">
                  <div className="testimonial-avatar" style={{ background: t.color }}>
                    {t.initials}
                  </div>
                  <div>
                    <div className="testimonial-name">{t.name}</div>
                    <div className="testimonial-role">{t.role}</div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default HomePage;
