"""Authentication request schemas."""

import re

from pydantic import BaseModel, EmailStr, field_validator

from constants.validation_constants import (
    INVALID_PHONE,
    NAME_MIN_LENGTH,
    NAME_ONLY_ALPHABETS,
    PASSWORD_LENGTH,
    PASSWORD_SPECIAL_CHARACTER,
    PASSWORD_UPPERCASE,
)


class BaseUserRegisterRequest(BaseModel):
    """Shared validation for registration requests."""

    full_name: str
    email: EmailStr
    password: str
    phone: str

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, value: str) -> str:
        """Ensure the name is long enough and alphabetic."""

        if len(value.strip()) < 2:
            raise ValueError(NAME_MIN_LENGTH)

        if not re.fullmatch(r"[A-Za-z\s]+", value):
            raise ValueError(NAME_ONLY_ALPHABETS)

        return value

    @field_validator("phone")
    @classmethod
    def validate_phone(cls, value: str) -> str:
        """Ensure the phone number has exactly 10 digits."""

        if not re.fullmatch(r"\d{10}", value):
            raise ValueError(INVALID_PHONE)

        return value

    @field_validator("password")
    @classmethod
    def validate_password(cls, value: str) -> str:
        """Ensure the password follows the required complexity rules."""

        if len(value) < 8 or len(value) > 12:
            raise ValueError(PASSWORD_LENGTH)

        if not re.search(r"[A-Z]", value):
            raise ValueError(PASSWORD_UPPERCASE)

        if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", value):
            raise ValueError(PASSWORD_SPECIAL_CHARACTER)

        return value


class PatientRegisterRequest(BaseUserRegisterRequest):
    """Registration payload for a patient account."""

    gender: str
    date_of_birth: str


class DoctorRegisterRequest(BaseUserRegisterRequest):
    """Registration payload for a doctor account."""

    qualification: str
    experience: int
    license_number: str
    specialization: str
    consultation_fee: float
    clinic_address: str


class LoginRequest(BaseModel):
    """Login payload with credentials."""

    email: EmailStr
    password: str