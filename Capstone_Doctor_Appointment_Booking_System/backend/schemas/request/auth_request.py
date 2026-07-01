import re

from pydantic import BaseModel, EmailStr, field_validator


class BaseUserRegisterRequest(BaseModel):
    full_name: str
    email: EmailStr
    password: str
    phone: str

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, value: str) -> str:
        if len(value.strip()) < 2:
            raise ValueError("Name must be at least 2 characters")

        if not re.fullmatch(r"[A-Za-z\s]+", value):
            raise ValueError("Name must contain alphabets only")

        return value

    @field_validator("phone")
    @classmethod
    def validate_phone(cls, value: str) -> str:
        if not re.fullmatch(r"\d{10}", value):
            raise ValueError("Phone must be exactly 10 digits")

        return value

    @field_validator("password")
    @classmethod
    def validate_password(cls, value: str) -> str:
        if len(value) < 8 or len(value) > 12:
            raise ValueError(
                "Password must be between 8 and 12 characters"
            )

        if not re.search(r"[A-Z]", value):
            raise ValueError(
                "Password must contain at least one uppercase letter"
            )

        if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", value):
            raise ValueError(
                "Password must contain at least one special character"
            )

        return value


class PatientRegisterRequest(BaseUserRegisterRequest):
    gender: str
    date_of_birth: str


class DoctorRegisterRequest(BaseUserRegisterRequest):
    qualification: str
    experience: int
    license_number: str
    specialization: str
    consultation_fee: float
    clinic_address: str


class LoginRequest(BaseModel):
    email: EmailStr
    password: str