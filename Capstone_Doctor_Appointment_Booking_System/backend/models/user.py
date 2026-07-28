"""User account model."""

from datetime import datetime

from beanie import Document
from pydantic import EmailStr, Field

from enums.user_role import UserRole


class User(Document):
    """Stored user account record."""

    full_name: str
    email: EmailStr
    password_hash: str
    phone: str
    role: UserRole
    is_active: bool = True
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        """Beanie collection settings."""

        name = "users"