"""JWT creation and decoding helpers."""

import jwt
from datetime import datetime, timedelta

from config.settings import settings


def create_access_token(
    user_id: str,
    email: str,
    role: str,
) -> str:
    """Create a signed access token for the authenticated user."""

    payload = {
        "sub": user_id,
        "email": email,
        "role": role,
        "iat": datetime.utcnow(),
        "exp": datetime.utcnow()
        + timedelta(
            minutes=settings.jwt_expiry_minutes
        ),
    }

    return jwt.encode(
        payload,
        settings.jwt_secret,
        algorithm=settings.jwt_algorithm,
    )


def decode_access_token(
    token: str,
) -> dict:
    """Decode an access token and return its payload."""

    try:
        return jwt.decode(
            token,
            settings.jwt_secret,
            algorithms=[settings.jwt_algorithm],
        )

    except jwt.ExpiredSignatureError:
        raise ValueError("Token has expired")

    except jwt.InvalidTokenError:
        raise ValueError("Invalid token")