# Interview Process Tracking Web App

A comprehensive web-based application for managing job applications and tracking candidates through different interview stages. Built with modern web technologies and enterprise-grade backend architecture.

---

## Table of Contents
- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [System Architecture](#system-architecture)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Testing & Coverage](#testing--coverage)
- [Security](#security)
- [Development Guidelines](#development-guidelines)
- [Submission Checklist](#submission-checklist)

---

## Overview

The Interview Tracking System enables organizations to:
- Manage job descriptions and candidates
- Track candidates through 5 interview stages (Profiling → Screening → L1 → L2 → HR Round)
- Schedule and conduct technical and HR interviews
- Collect panel feedback and drive hiring decisions
- Control workflows with role-based access

### Scope Highlights
- Single application per candidate
- Max 2 panel members per interview
- Resume upload (PDF only)
- Basic email verification
- Role-based access: HR, Panel, Candidate

---

## Tech Stack

### Frontend
- **HTML5** / **CSS3** / **Vanilla JavaScript** (no frameworks)
- **CSS Modules** for component styling
- **Fetch API** for HTTP requests
- **LocalStorage** for session management

### Backend
- **Java 17** with **Spring Boot 3.2.5**
- **Spring Security** (JWT-based authentication)
- **JPA / Hibernate** for ORM
- **PostgreSQL** for data persistence
- **Maven** for build management
- **SLF4J / Logback** for logging

### Testing & Quality
- **JUnit 5** / **Mockito** for unit tests
- **JaCoCo** for code coverage (80%+ target)
- **SpotBugs**, **PMD**, **Checkstyle** for code quality
- **MockMvc** for integration tests

### DevOps & Infrastructure
- **GitHub** for version control (public repository)
- **Maven Wrapper** for consistent builds
- **SMTP** for email notifications
- **Google Drive API** for resume storage

---

## Project Structure

```
Capstone_InterviewTracker/
├── README.md                     # Root documentation (this file)
├── frontend/
│   ├── README.md                # Frontend-specific setup
│   ├── index.html               # Landing page
│   ├── pages/                   # Role-specific pages (auth/, hr/, panel/, candidate/)
│   ├── js/
│   │   ├── services/            # API calls, auth state
│   │   ├── features/            # Page-specific logic
│   │   └── utils.js             # Shared utilities
│   └── css/                     # Global & component styles
│
└── backend/
    ├── README.md                # Backend-specific setup
    ├── pom.xml                  # Maven dependencies
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/nucleusteq/interviewtracker/
    │   │   │       ├── controller/       # REST endpoints
    │   │   │       ├── service/         # Business logic
    │   │   │       ├── repository/      # Data access
    │   │   │       ├── entity/          # JPA entities
    │   │   │       ├── dto/             # Transfer objects
    │   │   │       ├── security/        # JWT & auth
    │   │   │       ├── exception/       # Global handlers
    │   │   │       └── util/            # Utilities
    │   │   └── resources/
    │   │       ├── application.properties
    │   │       └── client_secret.json   # Google Drive creds
    │   └── test/java/                   # JUnit tests
    └── target/                          # Build outputs
```

---

## Quick Start

### Prerequisites
- Java 17 (or JDK 17+)
- PostgreSQL 12+ (or compatible DB)
- Python 3 (for frontend server, optional)
- Git

### 1. Clone Repository
```bash
git clone https://github.com/<your-username>/Capstone_InterviewTracker.git
cd Capstone_InterviewTracker
```

### 2. Backend Setup
See [backend/README.md](backend/README.md) for detailed instructions.

**Quick:**
```bash
cd backend
# Create PostgreSQL database
# Update src/main/resources/application.properties with DB credentials

# Run the app
.\mvnw.cmd spring-boot:run          # Windows
./mvnw spring-boot:run              # Unix/Mac

# Backend runs on http://localhost:8080/api
```

### 3. Frontend Setup
See [frontend/README.md](frontend/README.md) for detailed instructions.

**Quick:**
```bash
cd frontend
# Serve frontend files (choose one)
python -m http.server 8000
# or
npx http-server -p 8000

# Open http://localhost:8000 in browser
```

### 4. Test Login
- Email: `admin@example.com` (or use signup to create candidate)
- Password: `admin123` (or set your own)

---

## Key Features

### For HR
- ✅ Create and manage Job Descriptions (JD)
- ✅ Manage candidate profiles and progress
- ✅ Onboard panel members (send activation emails)
- ✅ Schedule interviews (L1, L2 with panel assignment)
- ✅ Review panel feedback and make hiring decisions
- ✅ Control stage progression manually (Profiling → Screening → L1 → L2 → HR)
- ✅ Filter and search candidates

### For Panel Members
- ✅ View assigned interviews
- ✅ Access candidate profile, resume, and focus areas
- ✅ Submit feedback (comments, strengths, weaknesses, rating 1-5)
- ✅ Make selection/rejection decision

### For Candidates
- ✅ View available job descriptions
- ✅ Apply for jobs with profile information
- ✅ Upload resume (PDF only)
- ✅ Track current interview stage
- ✅ View scheduled interviews (date, time, assigned panels)
- ✅ Cannot view panel feedback (secure)

---

## System Architecture

### High-Level Flow
```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (HTML/CSS/JS)                  │
│  Login → Dashboard (HR/Panel/Candidate) → Feature Pages    │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API Calls (JSON)
                         │ JWT Authorization Header
                         ▼
┌─────────────────────────────────────────────────────────────┐
│         BACKEND (Spring Boot 3.2.5 on Java 17)             │
│                                                              │
│  ┌───────────────┐  ┌──────────────┐  ┌───────────────┐   │
│  │ Controllers   │──│ Services     │──│ Repositories  │   │
│  │ (Endpoints)   │  │ (Business)   │  │ (Data Layer)  │   │
│  └───────────────┘  └──────────────┘  └───────────────┘   │
│         │                  │                  │             │
│         └──────────────────┴──────────────────┘             │
│                    ▼                                         │
│         ┌──────────────────────┐                            │
│         │  JPA / Hibernate ORM │                            │
│         └──────────────────────┘                            │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
            ┌────────────────────────┐
            │  PostgreSQL Database   │
            │  (Candidate, Interview,│
            │   Panel, Feedback,     │
            │   JD, User entities)   │
            └────────────────────────┘
```

### Security Architecture
- **JWT Token-based**: Issued on login, verified on each request
- **Role-based Access Control (RBAC)**: HR, PANEL, CANDIDATE
- **Password Encoding**: Base64 obfuscation on wire (bcrypt storage backend)
- **API-level Authorization**: Each endpoint checks user role
- **Secure Resume Storage**: PDF files uploaded to Google Drive

---

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login (email + password) |
| POST | `/api/auth/signup` | Candidate signup |
| POST | `/api/auth/activate?token=xxx&password=xxx` | Activate panel/candidate account |

### Job Descriptions (JD)
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/jd/all` | List all public JDs | Public |
| GET | `/api/jd/{id}` | Get JD details | Public |
| POST | `/api/hr/jd` | Create new JD | HR only |
| PUT | `/api/hr/jd/{id}` | Update JD | HR only |
| DELETE | `/api/hr/jd/{id}` | Delete JD | HR only |

### Candidates
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/candidate/register` | Candidate applies for job | Public |
| GET | `/api/hr/candidates` | List all candidates | HR only |
| GET | `/api/hr/candidate/{id}` | Get candidate details | HR only |
| PUT | `/api/hr/candidate/{id}/stage?stage=xxx` | Progress candidate to next stage | HR only |
| DELETE | `/api/hr/candidate/{id}` | Delete candidate | HR only |
| GET | `/api/candidate/profile` | Get my profile (logged-in candidate) | Candidate |
| PUT | `/api/candidate/profile` | Update my profile | Candidate |

### Interviews
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/hr/interview` | Schedule interview (L1/L2) | HR only |
| GET | `/api/hr/interview/{id}` | Get interview details | HR only |
| GET | `/api/panel/interviews` | Get my assigned interviews | Panel |
| GET | `/api/candidate/interviews` | Get my interview schedules | Candidate |
| PUT | `/api/panel/interview/{id}/feedback` | Submit panel feedback | Panel |
| PUT | `/api/hr/interview/{id}/hr-feedback` | Submit HR feedback | HR |

### Panel Members
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/hr/panel` | Create panel member | HR only |
| GET | `/api/hr/panels` | List all panel members | HR only |
| GET | `/api/hr/panel/{id}` | Get panel member details | HR only |
| PUT | `/api/hr/panel/{id}` | Update panel member | HR only |
| DELETE | `/api/hr/panel/{id}` | Delete panel member | HR only |
| GET | `/api/panel/profile` | Get my profile (logged-in panel) | Panel |

### Resume Upload
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/candidate/resume/{candidateId}` | Upload resume | HR only |
| POST | `/api/candidate/profile/resume` | Upload my resume | Candidate |

---

## Database Schema

### Core Entities
- **User**: id, email, password, role (ENUM: HR/PANEL/CANDIDATE), isActive, activationToken
- **Candidate**: id, fullName, mobile, dob, experience, currentCTC, expectedCTC, ...
- **PanelMember**: id, fullName, mobile, organization, designation, ...
- **JobDescription**: id, title, description, skillsRequired[], experienceRange, salaryRange, location, jobType
- **Interview**: id, candidateId, jdId, stage (L1/L2), date, time, panelMembers[], focusAreas
- **Feedback**: id, interviewId, panelMemberId, comments, strengths, weaknesses, rating, status (SELECTED/REJECTED)

---

## Testing & Coverage

### Run Tests
```bash
cd backend
./mvnw test                    # Run all unit tests
./mvnw clean test jacoco:report  # Generate coverage report
```

### Coverage Report
- Target: **80%+ code coverage**
- Report location: `backend/target/site/jacoco/index.html`
- Key areas covered:
  - Controllers (API endpoints)
  - Services (business logic)
  - Validators (input validation)
  - Exception handlers

### Test Files
- Unit tests for Services: `backend/src/test/java/.../service/`
- Controller tests: `backend/src/test/java/.../controller/`
- Entity tests: `backend/src/test/java/.../entity/`

---

## Security

### Password Handling
- Frontend: Base64-obfuscates passwords on the wire (prevents casual observation)
- Backend: Stores passwords using **bcrypt** hashing (never plaintext in DB)
- Activation: Secure token-based flow with email verification

### Authentication
- JWT tokens issued on successful login
- Token stored in `localStorage` (frontend)
- Token validated on every API request (backend)
- Expired tokens trigger re-login

### Authorization
- Role-based access control (@PreAuthorize annotations)
- Each endpoint verifies user role before executing
- Unauthorized requests return HTTP 403 Forbidden

### Resume Security
- Resumes uploaded to Google Drive (not on server)
- Only authorized users can access resume URLs
- PDF files only (validated server-side)

---

## Development Guidelines

### Code Standards (Backend)
- Follow **Spring Boot best practices**: Layered architecture (Controller → Service → Repository)
- Use **validation annotations**: @NotNull, @Email, @Size, etc.
- **Centralized exception handling** via GlobalExceptionHandler
- **Logging**: Use SLF4J logger on every class
- **DTOs**: Use DTO pattern for API requests/responses (never expose entities directly)

### Code Standards (Frontend)
- **Separate concerns**: HTML (structure), CSS (styles), JS (logic)
- **Reusable components**: Avoid code duplication (e.g., shared modals, cards)
- **Consistent naming**: camelCase for variables/functions, kebab-case for CSS classes
- **Comments**: Document complex logic

### Branching Strategy
- **main**: Stable, production-ready code (PR reviews required)
- **develop**: Integration branch for daily changes
- **Feature branches**: Create from `develop` for new features, merge back via PR

### Commit Guidelines
- Write clear, concise commit messages: `"feat: add login API"`, `"fix: panel feedback validation"`
- Commit frequently (small, logical changes)
- Review and test before pushing

---

## Submission Checklist

✅ **Repository Setup**
- [x] GitHub repository is public
- [x] Two branches: `main` and `develop`
- [x] Folder structure: `/backend` and `/frontend`

✅ **Code Quality**
- [x] Clean, readable code (Java conventions, JS best practices)
- [x] No code duplication
- [x] Proper naming conventions

✅ **Testing & Coverage**
- [x] Unit tests for core services
- [x] 80%+ code coverage
- [x] JaCoCo report generated

✅ **Documentation**
- [x] Root README (this file) with setup instructions
- [x] Frontend README with frontend-specific setup
- [x] Backend README with backend-specific setup

✅ **Functional Requirements** (from SRS)
- [x] JD Management (create, view, list)
- [x] Candidate Profiling (apply, upload resume, prevent duplicates)
- [x] Interview Stages (5 stages: Profiling → Screening → L1 → L2 → HR)
- [x] Candidate Tracking (view stage, progress, interview schedules)
- [x] Panel Creation & Onboarding (email with activation link)
- [x] Panel Assignment (1-2 panels per interview)
- [x] Panel Feedback (comments, rating, selection/rejection)
- [x] HR Controls (manage full workflow, stage progression, final decision)

✅ **Security**
- [x] JWT-based authentication
- [x] Role-based access control (HR, Panel, Candidate)
- [x] Password encoding (Base64 on wire, bcrypt in storage)
- [x] Secure resume storage (Google Drive)

✅ **Performance**
- [x] API response time < 2 seconds
- [x] Efficient database queries (with indexes)
- [x] No performance degradation with large candidate pools

✅ **Deliverables**
- [ ] Architectural Walkthrough (PPT/Doc with system design, components, strategy, UI screenshots)
- [ ] PR from develop → main (daily updates for buddy review)
- [ ] End-to-end functionality verified

---

## Key Technologies & Rationale

| Technology | Why Used |
|------------|----------|
| Spring Boot | Enterprise-grade framework with built-in security & data access |
| JPA/Hibernate | Simplifies ORM and reduces SQL boilerplate |
| PostgreSQL | Reliable, open-source relational DB with strong constraints |
| JWT | Stateless authentication for REST APIs |
| Maven | Declarative build management, plugin ecosystem |
| JUnit/Mockito | Industry-standard testing frameworks |
| SLF4J | Flexible, simple logging abstraction |
| Google Drive API | Secure, scalable file storage for resumes |

---

## Troubleshooting

### Backend won't start
- Check PostgreSQL is running: `psql -U postgres`
- Verify `application.properties` has correct DB credentials
- Check port 8080 is free: `netstat -ano | findstr :8080`

### Frontend can't reach backend
- Verify backend is running on `http://localhost:8080/api`
- Check browser console (F12) for CORS errors
- Verify `BASE_URL` in `frontend/js/services/api.js` is correct

### Tests fail
- Ensure test database is set up in `application.properties`
- Check mock configurations match actual service signatures
- Run: `./mvnw clean test -DskipTests=false`

---

## Support & Contact

For issues, questions, or improvements:
1. Check this README first
2. See [backend/README.md](backend/README.md) or [frontend/README.md](frontend/README.md) for component-specific help
3. Review the SRS document for requirements clarification
4. Reach out to the training team via GitHub issues or PR comments

---

## License

This project is part of the Capstone training program. Use for educational purposes only.
