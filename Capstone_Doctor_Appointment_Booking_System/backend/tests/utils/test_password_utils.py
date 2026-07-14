"""Tests for password utility functions."""

from utils.password_utils import (
    hash_password,
    verify_password,
)


def test_hash_password_returns_different_string():
    """Hash should not equal the original password."""

    password = "Admin@123"

    hashed = hash_password(password)

    assert hashed != password
    assert isinstance(hashed, str)


def test_verify_password_returns_true_for_correct_password():
    """verify_password should return True for the correct password."""

    password = "Admin@123"

    hashed = hash_password(password)

    assert verify_password(password, hashed) is True


def test_verify_password_returns_false_for_wrong_password():
    """verify_password should return False for an incorrect password."""

    password = "Admin@123"

    hashed = hash_password(password)

    assert verify_password("WrongPassword", hashed) is False


def test_hash_password_generates_unique_hashes():
    """Hashing the same password twice should generate different hashes."""

    password = "Admin@123"

    hash1 = hash_password(password)
    hash2 = hash_password(password)

    assert hash1 != hash2


def test_verify_password_multiple_hashes():
    """Both hashes of the same password should verify correctly."""

    password = "Admin@123"

    hash1 = hash_password(password)
    hash2 = hash_password(password)

    assert verify_password(password, hash1)
    assert verify_password(password, hash2)