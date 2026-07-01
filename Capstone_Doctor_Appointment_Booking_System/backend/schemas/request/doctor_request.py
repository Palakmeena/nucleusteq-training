"""Doctor request schemas."""

from typing import Literal

from pydantic import BaseModel, field_validator


Specialization = Literal[
    "Cardiologist",
    "Dermatologist",
    "Dentist",
    "Neurologist",
    "Orthopedic",
    "General Physician",
    "Pediatrician",
    "Gynecologist",
    "Psychiatrist",
    "Ophthalmologist",
]


class DoctorProfileRequest(BaseModel):
    """Payload for creating a doctor profile."""

    full_name: str
    phone: str
    qualification: str
    experience: int
    license_number: str
    specialization: Specialization
    consultation_fee: float
    clinic_address: str

    @field_validator("experience")
    @classmethod
    def validate_experience(cls, value: int) -> int:
        """Ensure experience is not negative."""

        if value < 0:
            raise ValueError("Experience cannot be negative")
        return value

    @field_validator("consultation_fee")
    @classmethod
    def validate_consultation_fee(cls, value: float) -> float:
        """Ensure consultation fee is positive."""

        if value <= 0:
            raise ValueError("Consultation fee must be greater than 0")
        return value


class DoctorUpdateRequest(BaseModel):
    """Payload for updating a doctor profile."""

    qualification: str | None = None
    experience: int | None = None
    specialization: Specialization | None = None
    consultation_fee: float | None = None
    clinic_address: str | None = None

    @field_validator("experience")
    @classmethod
    def validate_experience(cls, value):
        """Ensure experience is not negative when provided."""

        if value is not None and value < 0:
            raise ValueError("Experience cannot be negative")
        return value

    @field_validator("consultation_fee")
    @classmethod
    def validate_consultation_fee(cls, value):
        """Ensure consultation fee is positive when provided."""

        if value is not None and value <= 0:
            raise ValueError("Consultation fee must be greater than 0")
        return value