"""Appointment request schemas."""

from datetime import datetime
from typing import Literal

from pydantic import BaseModel, field_validator


class AppointmentBookRequest(BaseModel):
    """Payload for booking an appointment."""

    doctor_id: str
    slot_id: str
    appointment_date: str

    @field_validator("appointment_date")
    @classmethod
    def validate_appointment_date(cls, value: str) -> str:
        """Ensure the appointment date uses the expected format and is not in the past."""

        try:
            appointment_date = datetime.strptime(
                value, "%Y-%m-%d"
            ).date()
        except ValueError:
            raise ValueError(
                "Date must be in YYYY-MM-DD format"
            )

        if appointment_date < datetime.utcnow().date():
            raise ValueError(
                "Appointment date cannot be in the past"
            )

        return value


class AppointmentStatusRequest(BaseModel):
    """Payload for updating an appointment status."""

    status: Literal["COMPLETED", "NO_SHOW"]