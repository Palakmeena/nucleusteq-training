"""Authentication response schemas."""

from datetime import datetime

from pydantic import BaseModel, EmailStr

from enums.user_role import UserRole


class UserResponse(BaseModel):
    """Response payload for a user account."""

    id: str
    full_name: str
    email: EmailStr
    phone: str
    role: UserRole
    is_active: bool
    created_at: datetime

    model_config = {
        "from_attributes": True
    }


class LoginResponse(BaseModel):
    """Response payload for a successful login."""

    access_token: str
    token_type: str = "bearer"
    role: UserRole
    user_id: str
    full_name: str