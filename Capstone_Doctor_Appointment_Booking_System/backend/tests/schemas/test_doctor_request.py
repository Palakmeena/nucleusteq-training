"""Tests for doctor request schemas."""

import pytest
from pydantic import ValidationError

from schemas.request.doctor_request import (
    DoctorProfileRequest,
    DoctorUpdateRequest,
)


def valid_profile():
    """Return a valid doctor payload."""

    return {
        "full_name": "John Doe",
        "phone": "9876543210",
        "qualification": "MBBS",
        "experience": 5,
        "license_number": "LIC12345",
        "specialization": "Cardiologist",
        "consultation_fee": 500.0,
        "clinic_address": "Indore",
    }


def test_doctor_profile_valid():
    """Valid doctor profile."""

    request = DoctorProfileRequest(**valid_profile())

    assert request.full_name == "John Doe"
    assert request.experience == 5
    assert request.consultation_fee == 500.0


def test_doctor_profile_negative_experience():
    """Experience cannot be negative."""

    data = valid_profile()
    data["experience"] = -1

    with pytest.raises(ValidationError):
        DoctorProfileRequest(**data)


def test_doctor_profile_zero_fee():
    """Consultation fee must be positive."""

    data = valid_profile()
    data["consultation_fee"] = 0

    with pytest.raises(ValidationError):
        DoctorProfileRequest(**data)


def test_doctor_profile_negative_fee():
    """Negative consultation fee."""

    data = valid_profile()
    data["consultation_fee"] = -500

    with pytest.raises(ValidationError):
        DoctorProfileRequest(**data)


def test_doctor_profile_invalid_specialization():
    """Only predefined specializations are allowed."""

    data = valid_profile()
    data["specialization"] = "Engineer"

    with pytest.raises(ValidationError):
        DoctorProfileRequest(**data)


def test_doctor_update_valid():
    """Valid doctor update."""

    request = DoctorUpdateRequest(
        experience=10,
        consultation_fee=800,
        specialization="Dentist",
    )

    assert request.experience == 10
    assert request.consultation_fee == 800


def test_doctor_update_empty():
    """All update fields are optional."""

    request = DoctorUpdateRequest()

    assert request.experience is None
    assert request.consultation_fee is None
    assert request.specialization is None


def test_doctor_update_negative_experience():
    """Negative experience should fail."""

    with pytest.raises(ValidationError):
        DoctorUpdateRequest(
            experience=-2,
        )


def test_doctor_update_zero_fee():
    """Zero fee should fail."""

    with pytest.raises(ValidationError):
        DoctorUpdateRequest(
            consultation_fee=0,
        )


def test_doctor_update_negative_fee():
    """Negative fee should fail."""

    with pytest.raises(ValidationError):
        DoctorUpdateRequest(
            consultation_fee=-100,
        )


def test_doctor_update_invalid_specialization():
    """Invalid specialization."""

    with pytest.raises(ValidationError):
        DoctorUpdateRequest(
            specialization="Teacher",
        )