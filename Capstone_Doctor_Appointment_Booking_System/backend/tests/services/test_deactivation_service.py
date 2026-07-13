"""Tests for deactivation service."""

from datetime import datetime
from unittest.mock import AsyncMock, patch

import pytest

from enums.doctor_status import DeactivationRequestStatus
from exceptions.deactivation_exceptions import (
    InvalidDateRangeException,
)
from exceptions.doctor_exceptions import (
    DoctorNotFoundException,
)
from schemas.request.deactivation_request import (
    DeactivationRequestCreate,
)
from services import deactivation_service


@pytest.mark.asyncio
async def test_submit_deactivation_request_success():
    """Should submit a deactivation request."""

    request = DeactivationRequestCreate(
        start_date="2026-08-01",
        end_date="2026-08-05",
        reason="Vacation",
    )

    fake_doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    fake_request = type(
        "Request",
        (),
        {
            "id": "req1",
            "doctor_id": "doctor1",
            "user_id": "user1",
            "start_date": request.start_date,
            "end_date": request.end_date,
            "reason": request.reason,
            "status": DeactivationRequestStatus.PENDING,
            "created_at": datetime.utcnow(),
            "reviewed_at": None,
        },
    )()

    with patch.object(
        deactivation_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=fake_doctor),
    ), patch.object(
        deactivation_service.deactivation_repo,
        "save",
        AsyncMock(),
    ), patch.object(
        deactivation_service,
        "DeactivationRequest",
        return_value=fake_request,
    ):

        response = await deactivation_service.submit_deactivation_request(
            "user1",
            request,
        )

    assert response.doctor_id == "doctor1"


@pytest.mark.asyncio
async def test_submit_deactivation_request_doctor_not_found():
    """Doctor not found."""

    request = DeactivationRequestCreate(
        start_date="2026-08-01",
        end_date="2026-08-05",
        reason="Vacation",
    )

    with patch.object(
        deactivation_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(DoctorNotFoundException):
            await deactivation_service.submit_deactivation_request(
                "user1",
                request,
            )


@pytest.mark.asyncio
async def test_submit_deactivation_request_invalid_date_range():
    """Start date cannot be after end date."""

    request = DeactivationRequestCreate(
        start_date="2026-08-10",
        end_date="2026-08-05",
        reason="Vacation",
    )

    fake_doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    with patch.object(
        deactivation_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=fake_doctor),
    ):

        with pytest.raises(InvalidDateRangeException):
            await deactivation_service.submit_deactivation_request(
                "user1",
                request,
            )


@pytest.mark.asyncio
async def test_get_my_deactivation_requests_success():
    """Should return all requests."""

    fake_doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    fake_request = type(
        "Request",
        (),
        {
            "id": "req1",
            "doctor_id": "doctor1",
            "user_id": "user1",
            "start_date": "2026-08-01",
            "end_date": "2026-08-05",
            "reason": "Vacation",
            "status": DeactivationRequestStatus.PENDING,
            "created_at": datetime.utcnow(),
            "reviewed_at": None,
        },
    )()

    with patch.object(
        deactivation_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=fake_doctor),
    ), patch.object(
        deactivation_service.deactivation_repo,
        "find_all_by_doctor",
        AsyncMock(return_value=[fake_request]),
    ):

        result = await deactivation_service.get_my_deactivation_requests(
            "user1",
        )

    assert len(result) == 1
    assert result[0].doctor_id == "doctor1"


@pytest.mark.asyncio
async def test_get_my_deactivation_requests_doctor_not_found():
    """Doctor not found."""

    with patch.object(
        deactivation_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(DoctorNotFoundException):
            await deactivation_service.get_my_deactivation_requests(
                "user1",
            )


@pytest.mark.asyncio
async def test_reactivate_self_success():
    """Doctor should be reactivated."""

    fake_doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
            "is_active": False,
            "unavailable_from": "2026-08-01",
            "unavailable_to": "2026-08-05",
        },
    )()

    with patch.object(
        deactivation_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=fake_doctor),
    ), patch.object(
        deactivation_service.doctor_repo,
        "update",
        AsyncMock(),
    ) as update_mock:

        result = await deactivation_service.reactivate_self(
            "user1",
        )

    assert fake_doctor.is_active is True
    assert fake_doctor.unavailable_from is None
    assert fake_doctor.unavailable_to is None
    update_mock.assert_awaited_once()
    assert "message" in result


@pytest.mark.asyncio
async def test_reactivate_self_doctor_not_found():
    """Doctor not found."""

    with patch.object(
        deactivation_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(DoctorNotFoundException):
            await deactivation_service.reactivate_self(
                "user1",
            )