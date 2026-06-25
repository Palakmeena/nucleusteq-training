from pydantic import BaseModel, EmailStr
from datetime import datetime


class UserResponse(BaseModel):
    id: str
    full_name: str
    email: EmailStr
    phone: str
    role: str
    is_active: bool
    created_at: datetime

    model_config = {"from_attributes": True}


class LoginResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    role: str
    user_id: str
    full_name: str