from beanie import Document
from pydantic import EmailStr, Field
from datetime import datetime
from enum import Enum


class Role(str, Enum):
    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"
    ADMIN = "ADMIN"


class User(Document):
    full_name: str
    email: EmailStr
    password_hash: str
    phone: str
    role: Role
    is_active: bool = True
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "users"