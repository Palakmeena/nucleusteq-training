"""Appointment model."""

from datetime import datetime

from beanie import Document
from pydantic import Field

from enums.appointment_status import AppointmentStatus
from enums.payment_status import PaymentStatus


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