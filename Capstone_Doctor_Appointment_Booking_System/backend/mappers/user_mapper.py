"""Mapper for user responses."""

from models.user import User
from schemas.response.auth_response import UserResponse


class UserMapper:
    """Converts User documents into response DTOs."""

    @staticmethod
    def to_response(
        user: User,
        doctor_id: str | None = None,
    ) -> UserResponse:
        return UserResponse(
            id=str(user.id),
            full_name=user.full_name,
            email=user.email,
            phone=user.phone,
            role=user.role,
            is_active=user.is_active,
            created_at=user.created_at,
            doctor_id=doctor_id,
        )