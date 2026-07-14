"""Tests for user role enum."""

from enums.user_role import UserRole


def test_user_role_values():
    """Verify user role values."""

    assert UserRole.PATIENT.value == "PATIENT"
    assert UserRole.DOCTOR.value == "DOCTOR"
    assert UserRole.ADMIN.value == "ADMIN"


def test_user_role_member_count():
    """UserRole should contain three members."""

    assert len(UserRole) == 3


def test_user_role_lookup():
    """Lookup UserRole by value."""

    assert UserRole("PATIENT") == UserRole.PATIENT
    assert UserRole("DOCTOR") == UserRole.DOCTOR
    assert UserRole("ADMIN") == UserRole.ADMIN


def test_user_role_is_string_enum():
    """Enum values should be strings."""

    assert isinstance(UserRole.ADMIN.value, str)
    assert isinstance(UserRole.DOCTOR.value, str)