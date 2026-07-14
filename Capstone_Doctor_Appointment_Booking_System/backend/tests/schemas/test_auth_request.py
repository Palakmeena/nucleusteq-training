"""Tests for authentication request schemas."""

import pytest
from pydantic import ValidationError

from schemas.request.auth_request import (
    PatientRegisterRequest,
    DoctorRegisterRequest,
    LoginRequest,
)



def test_patient_register_request_valid():
    """Valid patient registration should succeed."""

    request = PatientRegisterRequest(
        full_name="Palak Meena",
        email="palak@example.com",
        password="Password@1",
        phone="9876543210",
        gender="Female",
        date_of_birth="2002-11-14",
    )

    assert request.full_name == "Palak Meena"
    assert request.email == "palak@example.com"


def test_patient_invalid_name_too_short():
    """Name must contain at least two characters."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="P",
            email="palak@example.com",
            password="Password@1",
            phone="9876543210",
            gender="Female",
            date_of_birth="2002-11-14",
        )


def test_patient_invalid_name_contains_numbers():
    """Name should contain only alphabets."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak123",
            email="palak@example.com",
            password="Password@1",
            phone="9876543210",
            gender="Female",
            date_of_birth="2002-11-14",
        )


def test_patient_invalid_phone():
    """Phone number must be exactly 10 digits."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="palak@example.com",
            password="Password@1",
            phone="12345",
            gender="Female",
            date_of_birth="2002-11-14",
        )


def test_patient_invalid_password_length():
    """Password should satisfy length validation."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="palak@example.com",
            password="Pass@1",
            phone="9876543210",
            gender="Female",
            date_of_birth="2002-11-14",
        )


def test_patient_password_requires_uppercase():
    """Password must contain an uppercase letter."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="palak@example.com",
            password="password@1",
            phone="9876543210",
            gender="Female",
            date_of_birth="2002-11-14",
        )


def test_patient_password_requires_special_character():
    """Password must contain a special character."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="palak@example.com",
            password="Password1",
            phone="9876543210",
            gender="Female",
            date_of_birth="2002-11-14",
        )


def test_patient_invalid_email():
    """Email should be validated."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="invalid-email",
            password="Password@1",
            phone="9876543210",
            gender="Female",
            date_of_birth="2002-11-14",
        )




def test_doctor_register_request_valid():
    """Valid doctor registration should succeed."""

    request = DoctorRegisterRequest(
        full_name="Dr Smith",
        email="doctor@example.com",
        password="Password@1",
        phone="9876543210",
        qualification="MBBS",
        experience=5,
        license_number="LIC12345",
        specialization="Cardiology",
        consultation_fee=500,
        clinic_address="Indore",
    )

    assert request.specialization == "Cardiology"
    assert request.consultation_fee == 500



def test_login_request_valid():
    """Valid login request."""

    request = LoginRequest(
        email="user@example.com",
        password="Password@1",
    )

    assert request.email == "user@example.com"


def test_login_invalid_email():
    """Invalid email should fail."""

    with pytest.raises(ValidationError):
        LoginRequest(
            email="wrong-email",
            password="Password@1",
        )


def test_login_missing_password():
    """Password is required."""

    with pytest.raises(ValidationError):
        LoginRequest(
            email="user@example.com",
        )


def test_patient_password_requires_lowercase():
    """Password should contain a lowercase letter."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="palak@example.com",
            password="PASSWORD@1",
            phone="9876543210",
            gender="Female",
            date_of_birth="2002-11-14",
        )


def test_patient_password_requires_number():
    """Password should contain a number."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="palak@example.com",
            password="Password@",
            phone="9876543210",
            gender="Female",
            date_of_birth="2002-11-14",
        )


def test_patient_invalid_gender():
    """Unsupported gender values should fail validation."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="palak@example.com",
            password="Password@1",
            phone="9876543210",
            gender="Unknown",
            date_of_birth="2002-11-14",
        )


@pytest.mark.parametrize("date_of_birth", ["not-a-date", "2999-01-01"])
def test_patient_invalid_date_of_birth(date_of_birth):
    """Date of birth must be a valid date in the past."""

    with pytest.raises(ValidationError):
        PatientRegisterRequest(
            full_name="Palak Meena",
            email="palak@example.com",
            password="Password@1",
            phone="9876543210",
            gender="Female",
            date_of_birth=date_of_birth,
        )


@pytest.mark.parametrize(
    "field",
    ["qualification", "license_number", "specialization", "clinic_address"],
)
def test_doctor_rejects_blank_text_fields(field):
    """Doctor profile text fields cannot be blank or whitespace only."""

    data = {
        "full_name": "Dr Smith",
        "email": "doctor@example.com",
        "password": "Password@1",
        "phone": "9876543210",
        "qualification": "MBBS",
        "experience": 5,
        "license_number": "LIC12345",
        "specialization": "Cardiology",
        "consultation_fee": 500,
        "clinic_address": "Indore",
    }
    data[field] = "   "

    with pytest.raises(ValidationError):
        DoctorRegisterRequest(**data)


def test_doctor_rejects_negative_experience():
    """A doctor cannot have negative experience."""

    with pytest.raises(ValidationError):
        DoctorRegisterRequest(
            full_name="Dr Smith",
            email="doctor@example.com",
            password="Password@1",
            phone="9876543210",
            qualification="MBBS",
            experience=-1,
            license_number="LIC12345",
            specialization="Cardiology",
            consultation_fee=500,
            clinic_address="Indore",
        )


@pytest.mark.parametrize("consultation_fee", [0, -100])
def test_doctor_requires_positive_consultation_fee(consultation_fee):
    """Consultation fee must be greater than zero."""

    with pytest.raises(ValidationError):
        DoctorRegisterRequest(
            full_name="Dr Smith",
            email="doctor@example.com",
            password="Password@1",
            phone="9876543210",
            qualification="MBBS",
            experience=5,
            license_number="LIC12345",
            specialization="Cardiology",
            consultation_fee=consultation_fee,
            clinic_address="Indore",
        )
