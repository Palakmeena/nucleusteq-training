from pydantic import BaseModel, field_validator
from datetime import datetime


class AppointmentBookRequest(BaseModel):
    doctor_id: str
    slot_id: str
    appointment_date: str

    @field_validator("appointment_date")
    @classmethod
    def validate_date(cls, v):
        try:
            appointment_date = datetime.strptime(v, "%Y-%m-%d").date()
        except ValueError:
            raise ValueError("Date must be in YYYY-MM-DD format")
        if appointment_date < datetime.utcnow().date():
            raise ValueError("Appointment date cannot be in the past")
        return v


class AppointmentStatusRequest(BaseModel):
    status: str

    @field_validator("status")
    @classmethod
    def validate_status(cls, v):
        valid = ["COMPLETED", "NO_SHOW"]
        if v not in valid:
            raise ValueError(f"Status must be one of: {', '.join(valid)}")
        return v