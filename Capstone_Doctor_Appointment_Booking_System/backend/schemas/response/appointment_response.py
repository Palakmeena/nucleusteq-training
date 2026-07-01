"""Appointment response schemas."""

from datetime import datetime

from pydantic import BaseModel

from models.appointment import AppointmentStatus, PaymentStatus


class AppointmentResponse(BaseModel):
    """Response payload for an appointment."""

    id: str
    patient_id: str
    doctor_id: str
    slot_id: str
    appointment_date: str
    start_time: str
    end_time: str
    status: AppointmentStatus
    payment_status: PaymentStatus
    created_at: datetime
    updated_at: datetime

    model_config = {
        "from_attributes": True
    }