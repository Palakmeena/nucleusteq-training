"""Application entrypoint for the doctor appointment backend."""

import uvicorn
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from config.settings import settings
from database.connection import connect_db

from models.user import User
from models.patient import Patient
from models.doctor import Doctor
from models.slot import Slot
from models.appointment import Appointment

from routers.auth_router import router as auth_router
from routers.admin_router import router as admin_router
from routers.doctor_router import router as doctor_router
from routers.slot_router import router as slot_router
from routers.appointment_router import router as appointment_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Connect to MongoDB on startup and close it on shutdown."""

    await connect_db(
        [
            User,
            Patient,
            Doctor,
            Slot,
            Appointment,
        ]
    )

    print("MongoDB connected")

    yield

    print("Application shutdown")


app = FastAPI(
    title="Doctor Appointment Booking System API",
    description="Backend API for Doctor Appointment Booking System",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth_router)
app.include_router(admin_router)
app.include_router(doctor_router)
app.include_router(slot_router)
app.include_router(appointment_router)


@app.get("/")
async def root():
    """Return a basic welcome message."""

    return {
        "message": "Doctor Appointment Booking System API"
    }


@app.get("/health")
async def health():
    """Return the service health status."""

    return {
        "status": "healthy"
    }


if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host=settings.app_host,
        port=settings.app_port,
        reload=True,
    )