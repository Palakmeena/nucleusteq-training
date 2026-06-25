from pydantic import BaseModel, EmailStr, field_validator
import re


class PatientRegisterRequest(BaseModel):
    full_name: str
    email: EmailStr
    password: str
    phone: str
    gender: str
    date_of_birth: str

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, v):
        if len(v) < 2:
            raise ValueError("Name must be at least 2 characters")
        if not re.match(r"^[A-Za-z\s]+$", v):
            raise ValueError("Name must contain alphabets only")
        return v

    @field_validator("phone")
    @classmethod
    def validate_phone(cls, v):
        if not re.match(r"^\d{10}$", v):
            raise ValueError("Phone must be exactly 10 digits")
        return v

    @field_validator("password")
    @classmethod
    def validate_password(cls, v):
        if len(v) < 8 or len(v) > 12:
            raise ValueError("Password must be between 8 and 12 characters")
        if not re.search(r"[A-Z]", v):
            raise ValueError("Password must contain at least one uppercase letter")
        if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", v):
            raise ValueError("Password must contain at least one special character")
        return v


class DoctorRegisterRequest(BaseModel):
    full_name: str
    email: EmailStr
    password: str
    phone: str

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, v):
        if len(v) < 2:
            raise ValueError("Name must be at least 2 characters")
        if not re.match(r"^[A-Za-z\s]+$", v):
            raise ValueError("Name must contain alphabets only")
        return v

    @field_validator("phone")
    @classmethod
    def validate_phone(cls, v):
        if not re.match(r"^\d{10}$", v):
            raise ValueError("Phone must be exactly 10 digits")
        return v

    @field_validator("password")
    @classmethod
    def validate_password(cls, v):
        if len(v) < 8 or len(v) > 12:
            raise ValueError("Password must be between 8 and 12 characters")
        if not re.search(r"[A-Z]", v):
            raise ValueError("Password must contain at least one uppercase letter")
        if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", v):
            raise ValueError("Password must contain at least one special character")
        return v