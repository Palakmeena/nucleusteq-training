# Doctor Appointment Booking System

A full-stack web application for managing doctor appointments. Patients can find approved doctors, view available slots, book and manage appointments, and complete a simulated payment flow. Doctors can maintain their profiles and availability, while administrators approve doctor registrations and manage doctor deactivation requests.

## Features

### Patients

- Register and sign in securely.
- Search approved doctors by name, specialization, location, experience, and consultation fee.
- View available doctor slots and book appointments.
- View appointment history and cancel eligible appointments.

### Doctors

- Register a doctor profile and wait for administrator approval.
- Manage profile details and appointment slots.
- View appointments and update appointment status.
- Submit temporary deactivation requests and reactivate the profile when needed.

### Administrators

- View dashboard statistics, users, doctors, and recent appointments.
- Approve or reject doctor registrations.
- Review, approve, or reject doctor deactivation requests.

## Technology Stack

| Layer | Technology |
| --- | --- |
| Frontend | React, Vite, Material UI, Axios |
| Backend | Python, FastAPI, Pydantic |
| Database | MongoDB, Motor, Beanie |
| Authentication | JWT, Passlib with bcrypt |
| Testing | pytest, pytest-asyncio, pytest-mock |

## Project Structure

```text
.
|-- backend/
|   |-- config/          # Environment-based application settings
|   |-- models/          # MongoDB document models
|   |-- schemas/         # Request and response validation models
|   |-- routers/         # FastAPI endpoints
|   |-- services/        # Application business logic
|   |-- repositories/    # Database access layer
|   |-- dependencies/    # Route dependencies, including authentication
|   |-- middleware/      # HTTP middleware, including CORS and request logging
|   |-- tests/           # Backend unit tests
|   `-- main.py          # Backend entry point
`-- frontend/
    `-- src/             # React application
```

## Prerequisites

- Python 3.10 or later
- Node.js 18 or later
- MongoDB running locally or a MongoDB connection URI

## Setup

### 1. Configure the backend

From the project root, create and activate a virtual environment:

```powershell
cd backend
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r requirements.txt
```

Create `backend/.env` with the following values:

```env
MONGO_URI=mongodb://localhost:27017
DB_NAME=doctor_appointment_db
JWT_SECRET=replace_with_a_long_random_secret
JWT_ALGORITHM=HS256
JWT_EXPIRY_MINUTES=30
APP_HOST=127.0.0.1
APP_PORT=8000
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173
```

Start the API:

```powershell
uvicorn main:app --reload
```

The API is available at `http://127.0.0.1:8000`. FastAPI documentation is available at `http://127.0.0.1:8000/docs`.

### 2. Configure the frontend

Open a second terminal from the project root:

```powershell
cd frontend
npm install
npm run dev
```

Vite serves the frontend locally and proxies `/api` requests to the backend at `http://127.0.0.1:8000`.

## Running Tests

Run backend tests from the `backend` directory after activating the virtual environment:

```powershell
python -m pytest
```

Build the frontend for production:

```powershell
cd frontend
npm run build
```

## API Overview

All API routes are prefixed with `/api/v1`.

| Area | Base route |
| --- | --- |
| Authentication | `/auth` |
| Doctors | `/doctors` |
| Slots | `/slots` |
| Appointments | `/appointments` |
| Doctor deactivation | `/doctors/deactivation` |
| Administration | `/admin` |

Protected endpoints require a bearer token returned by the login endpoint:

```http
Authorization: Bearer <access_token>
```

## Notes

- Doctor accounts must be approved by an administrator before they can be used for appointment booking.
- Environment files are excluded from version control. Do not commit database credentials or JWT secrets.
- The backend includes a local-development admin seed script at `backend/scripts/seed_admin.py`. Review and change its default credentials before using it outside local development.


# Docker deployment

This project runs entirely in Docker: the React frontend, FastAPI backend, and
MongoDB database are separate containers. MongoDB data is retained in Docker's
named `mongodb_data` volume.

## Run on a machine that may download images

1. In the project root, copy `.env.example` to `.env`.
2. Set `JWT_SECRET` in `.env` to a long random value. Do not commit this file.
3. Build and start the application:

   ```powershell
   docker compose up --build -d
   ```

4. Open `http://localhost:8080`. Stop it with `docker compose down`.

MongoDB is intentionally not exposed to the host. The frontend forwards API
requests to the backend internally.

## Offline company-laptop handoff

On the personal laptop, after completing the online build, export every image:

```powershell
docker compose build
docker pull mongo:7.0
docker save -o doctor-booking-images.tar doctor-booking-frontend:1.0 doctor-booking-backend:1.0 mongo:7.0
```

Copy the project folder, `doctor-booking-images.tar`, and a prepared `.env`
file (with the same secret) to the company laptop. On that laptop, open
PowerShell in the project root and run:

```powershell
docker load -i doctor-booking-images.tar
docker compose up -d --no-build
```

Then open `http://localhost:8080`. This path requires Docker Desktop but does
not install Node.js, Python, MongoDB, or project dependencies on the company
laptop, and does not download any images or packages there.

## Real-time updates

The application uses FastAPI's built-in WebSocket support for live appointment
and slot updates. After changing WebSocket code, rebuild the Docker services:

```powershell
docker compose up --build -d
```

To see it working, sign in to the same doctor profile in two browser sessions:

1. Sign in as a patient and open the doctor's profile page.
2. In an incognito window, sign in as the doctor and create, update, or delete
   a slot.
3. The patient's slot list updates without refreshing the page.

For appointment updates, keep the doctor's appointments page open in one
session and book or pay for an appointment as a patient in the other session.
The doctor's list refreshes automatically. When the doctor updates an
appointment status, the patient's appointments page refreshes automatically.

## Persistent data

`docker compose down` keeps database data. To intentionally delete all stored
application data, run `docker compose down -v`.

