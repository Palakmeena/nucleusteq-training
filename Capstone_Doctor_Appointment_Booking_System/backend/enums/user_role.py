from enum import Enum


class UserRole(str, Enum):
    """Supported application roles."""

    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"
    ADMIN = "ADMIN"