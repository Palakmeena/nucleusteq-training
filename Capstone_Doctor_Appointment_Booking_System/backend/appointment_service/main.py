import uvicorn
from fastapi import FastAPI
from contextlib import asynccontextmanager
from database.connection import connect_db
from models.doctor import Doctor
from models.slot import Slot
from models.appointment import Appointment
from config.settings import settings
from routers.doctor_router import router as doctor_router
from routers.slot_router import router as slot_router
from routers.appointment_router import router as appointment_router
from routers.admin_router import router as admin_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    await connect_db([Doctor, Slot, Appointment])
    print("✅ Connected to MongoDB — Appointment Service")
    yield
    print("❌ Disconnected from MongoDB — Appointment Service")


app = FastAPI(
    title="Appointment Service",
    description="Handles doctors, slots, and appointments",
    version="1.0.0",
    lifespan=lifespan
)

app.include_router(doctor_router)
app.include_router(slot_router)
app.include_router(appointment_router)
app.include_router(admin_router)


@app.get("/health")
async def health_check():
    return {"status": "Appointment Service is running", "port": 8002}


if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=settings.appointment_service_port,
        reload=True
    )