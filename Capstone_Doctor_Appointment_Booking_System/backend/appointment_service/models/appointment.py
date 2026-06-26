from beanie import Document
from pydantic import Field
from datetime import datetime
from enum import Enum


class AppointmentStatus(str, Enum):
    PENDING = "PENDING"
    CONFIRMED = "CONFIRMED"
    CANCELLED = "CANCELLED"
    COMPLETED = "COMPLETED"
    NO_SHOW = "NO_SHOW"


class PaymentStatus(str, Enum):
    PENDING = "PENDING"
    PAID = "PAID"


class Appointment(Document):
    patient_id: str
    doctor_id: str
    slot_id: str
    appointment_date: str
    start_time: str
    end_time: str
    status: AppointmentStatus = AppointmentStatus.PENDING
    payment_status: PaymentStatus = PaymentStatus.PENDING
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "appointments"