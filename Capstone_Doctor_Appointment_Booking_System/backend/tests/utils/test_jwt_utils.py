"""Tests for JWT utility functions."""

import jwt
import pytest
from datetime import datetime, timedelta

from config.settings import settings
from exceptions.auth_exceptions import InvalidTokenException
from utils.jwt_utils import (
    create_access_token,
    decode_access_token,
)


def test_create_access_token_returns_string():
    """Access token should be generated as a string."""

    token = create_access_token(
        user_id="123",
        email="test@example.com",
        role="PATIENT",
    )

    assert isinstance(token, str)
    assert token != ""


def test_decode_access_token_returns_payload():
    """Decoded token should contain the expected payload."""

    token = create_access_token(
        user_id="123",
        email="test@example.com",
        role="PATIENT",
    )

    payload = decode_access_token(token)

    assert payload["sub"] == "123"
    assert payload["email"] == "test@example.com"
    assert payload["role"] == "PATIENT"

    assert "iat" in payload
    assert "exp" in payload


def test_decode_invalid_token_raises_exception():
    """Invalid JWT should raise InvalidTokenException."""

    with pytest.raises(InvalidTokenException):
        decode_access_token("this-is-not-a-valid-token")


def test_decode_expired_token_raises_exception():
    """Expired JWT should raise InvalidTokenException."""

    payload = {
        "sub": "123",
        "email": "test@example.com",
        "role": "PATIENT",
        "iat": datetime.utcnow() - timedelta(minutes=10),
        "exp": datetime.utcnow() - timedelta(minutes=5),
    }

    expired_token = jwt.encode(
        payload,
        settings.jwt_secret,
        algorithm=settings.jwt_algorithm,
    )

    with pytest.raises(InvalidTokenException):
        decode_access_token(expired_token)


def test_create_tokens_with_different_users():
    """Tokens generated for different users should be different."""

    token1 = create_access_token(
        user_id="123",
        email="test@example.com",
        role="PATIENT",
    )

    token2 = create_access_token(
        user_id="456",
        email="doctor@example.com",
        role="DOCTOR",
    )

    assert token1 != token2