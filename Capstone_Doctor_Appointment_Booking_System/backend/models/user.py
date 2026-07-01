"""User account model and role definitions."""

from beanie import Document
from pydantic import EmailStr, Field
from datetime import datetime
from enum import Enum


class Role(str, Enum):
    """Supported application roles."""

    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"
    ADMIN = "ADMIN"


class User(Document):
    """Stored user account record."""

    full_name: str
    email: EmailStr
    password_hash: str
    phone: str
    role: Role
    is_active: bool = True
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        """Beanie collection settings."""

        name = "users"