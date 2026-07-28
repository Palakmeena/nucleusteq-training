"""Authentication request schemas."""

import re
from datetime import date

from pydantic import BaseModel, EmailStr, field_validator

from constants.validation_constants import (
    INVALID_PHONE,
    INVALID_DATE_OF_BIRTH,
    INVALID_GENDER,
    NAME_MIN_LENGTH,
    NAME_ONLY_ALPHABETS,
    PASSWORD_LENGTH,
    PASSWORD_LOWERCASE,
    PASSWORD_NUMBER,
    PASSWORD_SPECIAL_CHARACTER,
    PASSWORD_UPPERCASE,
    REQUIRED_TEXT_FIELD,
    CONSULTATION_FEE_POSITIVE,
    EXPERIENCE_CANNOT_BE_NEGATIVE,
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

        return value.strip()

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

        if not re.search(r"[a-z]", value):
            raise ValueError(PASSWORD_LOWERCASE)

        if not re.search(r"\d", value):
            raise ValueError(PASSWORD_NUMBER)

        if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", value):
            raise ValueError(PASSWORD_SPECIAL_CHARACTER)

        return value


class PatientRegisterRequest(BaseUserRegisterRequest):
    """Registration payload for a patient account."""

    gender: str
    date_of_birth: str

    @field_validator("gender")
    @classmethod
    def validate_gender(cls, value: str) -> str:
        """Allow only supported patient gender values."""

        normalized_value = value.strip().title()
        if normalized_value not in {"Male", "Female", "Other"}:
            raise ValueError(INVALID_GENDER)

        return normalized_value

    @field_validator("date_of_birth")
    @classmethod
    def validate_date_of_birth(cls, value: str) -> str:
        """Ensure the date of birth is valid and not in the future."""

        try:
            parsed_date = date.fromisoformat(value)
        except ValueError as error:
            raise ValueError(INVALID_DATE_OF_BIRTH) from error

        if parsed_date >= date.today():
            raise ValueError(INVALID_DATE_OF_BIRTH)

        return value


class DoctorRegisterRequest(BaseUserRegisterRequest):
    """Registration payload for a doctor account."""

    qualification: str
    experience: int
    license_number: str
    specialization: str
    consultation_fee: float
    clinic_address: str

    @field_validator(
        "qualification",
        "license_number",
        "specialization",
        "clinic_address",
    )
    @classmethod
    def validate_required_text_fields(cls, value: str) -> str:
        """Reject whitespace-only doctor profile fields."""

        if not value.strip():
            raise ValueError(REQUIRED_TEXT_FIELD)

        return value.strip()

    @field_validator("experience")
    @classmethod
    def validate_experience(cls, value: int) -> int:
        """Ensure experience is not negative."""

        if value < 0:
            raise ValueError(EXPERIENCE_CANNOT_BE_NEGATIVE)

        return value

    @field_validator("consultation_fee")
    @classmethod
    def validate_consultation_fee(cls, value: float) -> float:
        """Ensure a doctor consultation fee is positive."""

        if value <= 0:
            raise ValueError(CONSULTATION_FEE_POSITIVE)

        return value


class LoginRequest(BaseModel):
    """Login payload with credentials."""

    email: EmailStr
    password: str
