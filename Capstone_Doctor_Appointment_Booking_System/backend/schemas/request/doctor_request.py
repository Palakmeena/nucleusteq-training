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
        if value < 0:
            raise ValueError("Experience cannot be negative")
        return value

    @field_validator("consultation_fee")
    @classmethod
    def validate_consultation_fee(cls, value: float) -> float:
        if value <= 0:
            raise ValueError("Consultation fee must be greater than 0")
        return value


class DoctorUpdateRequest(BaseModel):
    qualification: str | None = None
    experience: int | None = None
    specialization: Specialization | None = None
    consultation_fee: float | None = None
    clinic_address: str | None = None

    @field_validator("experience")
    @classmethod
    def validate_experience(cls, value):
        if value is not None and value < 0:
            raise ValueError("Experience cannot be negative")
        return value

    @field_validator("consultation_fee")
    @classmethod
    def validate_consultation_fee(cls, value):
        if value is not None and value <= 0:
            raise ValueError("Consultation fee must be greater than 0")
        return value