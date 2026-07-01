"""Appointment model and related status enums."""

from beanie import Document
from pydantic import Field
from datetime import datetime
from enum import Enum


class AppointmentStatus(str, Enum):
    """Allowed appointment lifecycle states."""

    PENDING = "PENDING"
    CONFIRMED = "CONFIRMED"
    CANCELLED = "CANCELLED"
    COMPLETED = "COMPLETED"
    NO_SHOW = "NO_SHOW"


class PaymentStatus(str, Enum):
    """Allowed payment states."""

    PENDING = "PENDING"
    PAID = "PAID"


class Appointment(Document):
    """Stored appointment record."""

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
        """Beanie collection settings."""

        name = "appointments"