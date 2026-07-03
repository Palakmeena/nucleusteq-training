"""Authentication and role-based access helpers."""

from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from constants.auth_constants import AuthMessages
from utils.jwt_utils import decode_access_token

security = HTTPBearer()


def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security)
) -> dict:
    """Decode the bearer token and return the current user payload."""

    token = credentials.credentials
    try:
        payload = decode_access_token(token)
        return payload
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=str(e)
        )


def require_role(*roles: str):
    """Create a dependency that restricts access to specific roles."""

    def role_checker(current_user: dict = Depends(get_current_user)) -> dict:
        """Ensure the current user has one of the allowed roles."""

        if current_user.get("role") not in roles:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=AuthMessages.FORBIDDEN
            )
        return current_user
    return role_checker


require_admin = require_role("ADMIN")
require_doctor = require_role("DOCTOR")
require_patient = require_role("PATIENT")