"""Authentication and authorization dependencies for API routes."""

from fastapi import Depends
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

from exceptions.auth_exceptions import ForbiddenException, UnauthorizedException
from utils.jwt_utils import decode_access_token


security = HTTPBearer(auto_error=False)


def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security),
) -> dict:
    """Decode the bearer token and return the authenticated user's payload."""

    if credentials is None:
        raise UnauthorizedException()

    return decode_access_token(credentials.credentials)


def require_role(*roles: str):
    """Create a dependency that permits only users with the supplied roles."""

    def role_checker(current_user: dict = Depends(get_current_user)) -> dict:
        if current_user.get("role") not in roles:
            raise ForbiddenException()
        return current_user

    return role_checker


require_admin = require_role("ADMIN")
require_doctor = require_role("DOCTOR")
require_patient = require_role("PATIENT")
