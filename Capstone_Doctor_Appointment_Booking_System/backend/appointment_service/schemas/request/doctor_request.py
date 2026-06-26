from pydantic import BaseModel, field_validator
from typing import Optional


class DoctorProfileRequest(BaseModel):
    full_name: str
    phone: str
    qualification: str
    experience: int
    license_number: str
    specialization: str
    consultation_fee: float
    clinic_address: str

    @field_validator("consultation_fee")
    @classmethod
    def validate_fee(cls, v):
        if v <= 0:
            raise ValueError("Consultation fee must be greater than 0")
        return v

    @field_validator("experience")
    @classmethod
    def validate_experience(cls, v):
        if v < 0:
            raise ValueError("Experience cannot be negative")
        return v

    @field_validator("specialization")
    @classmethod
    def validate_specialization(cls, v):
        valid = [
            "Cardiologist", "Dermatologist", "Dentist",
            "Neurologist", "Orthopedic", "General Physician",
            "Pediatrician", "Gynecologist", "Psychiatrist",
            "Ophthalmologist"
        ]
        if v not in valid:
            raise ValueError(f"Specialization must be one of: {', '.join(valid)}")
        return v


class DoctorUpdateRequest(BaseModel):
    qualification: Optional[str] = None
    experience: Optional[int] = None
    specialization: Optional[str] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None