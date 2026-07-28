"""Tests for auth service."""

from datetime import datetime
from unittest.mock import AsyncMock, patch

import pytest

from enums.user_role import UserRole
from exceptions.auth_exceptions import (
    EmailAlreadyExistsException,
    InactiveAccountException,
    InvalidCredentialsException,
    UserNotFoundException,
)
from exceptions.doctor_exceptions import (
    DoctorProfileAlreadyExistsException,
)
from schemas.request.auth_request import (
    DoctorRegisterRequest,
    PatientRegisterRequest,
)
from services import auth_service


@pytest.mark.asyncio
async def test_register_patient_success():
    """Patient registration succeeds."""

    request = PatientRegisterRequest(
        full_name="John Doe",
        email="john@test.com",
        password="Password@1",
        phone="9876543210",
        gender="Male",
        date_of_birth="2000-01-01",
    )

    fake_user = type(
        "User",
        (),
        {
            "id": "user1",
            "email": request.email,
            "full_name": request.full_name,
            "phone": request.phone,
            "role": UserRole.PATIENT,
            "is_active": True,
            "created_at": datetime.utcnow(),
        },
    )()

    fake_patient = object()

    response = {"success": True}

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=None),
    ), patch.object(
        auth_service.user_repo,
        "save",
        AsyncMock(),
    ), patch.object(
        auth_service.patient_repo,
        "save",
        AsyncMock(),
    ), patch.object(
        auth_service.UserMapper,
        "to_response",
        return_value=response,
    ), patch.object(
        auth_service,
        "User",
        return_value=fake_user,
    ), patch.object(
        auth_service,
        "Patient",
        return_value=fake_patient,
    ):

        result = await auth_service.register_patient(request)

    assert result == response


@pytest.mark.asyncio
async def test_register_patient_duplicate_email():
    """Duplicate email raises exception."""

    request = PatientRegisterRequest(
        full_name="John",
        email="john@test.com",
        password="Password@1",
        phone="9876543210",
        gender="Male",
        date_of_birth="2000-01-01",
    )

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=object()),
    ):

        with pytest.raises(EmailAlreadyExistsException):
            await auth_service.register_patient(request)


@pytest.mark.asyncio
async def test_register_doctor_success():
    """Doctor registration succeeds."""

    request = DoctorRegisterRequest(
        full_name="Doctor",
        email="doctor@test.com",
        password="Password@1",
        phone="9876543210",
        qualification="MBBS",
        experience=5,
        license_number="LIC123",
        specialization="Dentist",
        consultation_fee=500,
        clinic_address="Indore",
    )

    fake_user = type(
        "User",
        (),
        {
            "id": "user1",
            "email": request.email,
            "full_name": request.full_name,
            "phone": request.phone,
            "role": UserRole.DOCTOR,
            "is_active": False,
            "created_at": datetime.utcnow(),
        },
    )()

    fake_doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    response = {"doctor": True}

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=None),
    ), patch.object(
        auth_service.doctor_repo,
        "find_by_license",
        AsyncMock(return_value=None),
    ), patch.object(
        auth_service.user_repo,
        "save",
        AsyncMock(),
    ), patch.object(
        auth_service.doctor_repo,
        "save",
        AsyncMock(),
    ), patch.object(
        auth_service.UserMapper,
        "to_response",
        return_value=response,
    ), patch.object(
        auth_service,
        "User",
        return_value=fake_user,
    ), patch.object(
        auth_service,
        "Doctor",
        return_value=fake_doctor,
    ):

        result = await auth_service.register_doctor(request)

    assert result == response


@pytest.mark.asyncio
async def test_register_doctor_duplicate_email():
    """Duplicate email raises exception."""

    request = DoctorRegisterRequest(
        full_name="Doctor",
        email="doctor@test.com",
        password="Password@1",
        phone="9876543210",
        qualification="MBBS",
        experience=5,
        license_number="LIC123",
        specialization="Dentist",
        consultation_fee=500,
        clinic_address="Indore",
    )

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=object()),
    ):

        with pytest.raises(EmailAlreadyExistsException):
            await auth_service.register_doctor(request)


@pytest.mark.asyncio
async def test_register_doctor_duplicate_license():
    """Duplicate license raises exception."""

    request = DoctorRegisterRequest(
        full_name="Doctor",
        email="doctor@test.com",
        password="Password@1",
        phone="9876543210",
        qualification="MBBS",
        experience=5,
        license_number="LIC123",
        specialization="Dentist",
        consultation_fee=500,
        clinic_address="Indore",
    )

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=None),
    ), patch.object(
        auth_service.doctor_repo,
        "find_by_license",
        AsyncMock(return_value=object()),
    ):

        with pytest.raises(DoctorProfileAlreadyExistsException):
            await auth_service.register_doctor(request)


# -----------------------------
# Login
# -----------------------------


@pytest.mark.asyncio
async def test_login_success():
    """Successful login."""

    fake_user = type(
        "User",
        (),
        {
            "id": "1",
            "email": "john@test.com",
            "password_hash": "hashed",
            "role": UserRole.PATIENT,
            "full_name": "John",
            "is_active": True,
        },
    )()

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=fake_user),
    ), patch.object(
        auth_service,
        "verify_password",
        return_value=True,
    ), patch.object(
        auth_service,
        "create_access_token",
        return_value="token123",
    ):

        response = await auth_service.login_user(
            "john@test.com",
            "Password@1",
        )

    assert response.access_token == "token123"
    assert response.user_id == "1"


@pytest.mark.asyncio
async def test_login_user_not_found():
    """Unknown user."""

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(UserNotFoundException):
            await auth_service.login_user(
                "abc@test.com",
                "Password@1",
            )


@pytest.mark.asyncio
async def test_login_wrong_password():
    """Wrong password."""

    fake_user = type(
        "User",
        (),
        {
            "password_hash": "hash",
            "email": "abc@test.com",
        },
    )()

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=fake_user),
    ), patch.object(
        auth_service,
        "verify_password",
        return_value=False,
    ):

        with pytest.raises(InvalidCredentialsException):
            await auth_service.login_user(
                "abc@test.com",
                "wrong",
            )


@pytest.mark.asyncio
async def test_login_inactive_user():
    """Inactive user cannot login."""

    fake_user = type(
        "User",
        (),
        {
            "password_hash": "hash",
            "email": "abc@test.com",
            "is_active": False,
        },
    )()

    with patch.object(
        auth_service.user_repo,
        "find_by_email",
        AsyncMock(return_value=fake_user),
    ), patch.object(
        auth_service,
        "verify_password",
        return_value=True,
    ):

        with pytest.raises(InactiveAccountException):
            await auth_service.login_user(
                "abc@test.com",
                "Password@1",
            )