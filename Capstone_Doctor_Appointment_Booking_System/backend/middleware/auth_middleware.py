"""Authentication and role-based access helpers."""

from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from exceptions.auth_exceptions import (
    ForbiddenException,
    UnauthorizedException,
)
from utils.jwt_utils import decode_access_token

security = HTTPBearer(auto_error=False)


def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security)
) -> dict:
    """Decode the bearer token and return the current user payload."""

    if credentials is None:
        raise UnauthorizedException()

    token = credentials.credentials

    payload = decode_access_token(token)
    return payload


def require_role(*roles: str):
    """Create a dependency that restricts access to specific roles."""

    def role_checker(current_user: dict = Depends(get_current_user)) -> dict:
        """Ensure the current user has one of the allowed roles."""

        if current_user.get("role") not in roles:
            raise ForbiddenException()
        return current_user
    return role_checker


require_admin = require_role("ADMIN")
require_doctor = require_role("DOCTOR")
require_patient = require_role("PATIENT")