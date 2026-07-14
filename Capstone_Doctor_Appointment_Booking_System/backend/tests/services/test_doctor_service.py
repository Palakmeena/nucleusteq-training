"""Tests for doctor_service.py"""

from unittest.mock import AsyncMock, patch

import pytest

from exceptions.doctor_exceptions import DoctorNotFoundException
from schemas.request.doctor_request import DoctorUpdateRequest
from services import doctor_service


@pytest.mark.asyncio
async def test_get_doctor_by_id_success():
    """Should return doctor by id."""

    doctor = object()
    response = {"id": "1"}

    with patch.object(
        doctor_service.doctor_repo,
        "find_by_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        doctor_service.DoctorMapper,
        "to_response",
        return_value=response,
    ):

        result = await doctor_service.get_doctor_by_id("1")

        assert result == response


@pytest.mark.asyncio
async def test_get_doctor_by_id_not_found():
    """Should raise when doctor does not exist."""

    with patch.object(
        doctor_service.doctor_repo,
        "find_by_id",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(DoctorNotFoundException):
            await doctor_service.get_doctor_by_id("1")


@pytest.mark.asyncio
async def test_search_doctors_returns_list():
    """Should return mapped doctors."""

    doctors = [object(), object()]

    with patch.object(
        doctor_service.doctor_repo,
        "search",
        AsyncMock(return_value=doctors),
    ), patch.object(
        doctor_service.DoctorMapper,
        "to_list_response",
        side_effect=[{"id": 1}, {"id": 2}],
    ):

        result = await doctor_service.search_doctors()

        assert len(result) == 2


@pytest.mark.asyncio
async def test_search_doctors_empty():
    """Should return empty list."""

    with patch.object(
        doctor_service.doctor_repo,
        "search",
        AsyncMock(return_value=[]),
    ):

        result = await doctor_service.search_doctors()

        assert result == []


@pytest.mark.asyncio
async def test_update_doctor_profile_success():
    """Should update doctor profile."""

    class FakeDoctor:
        user_id = "user1"
        qualification = "MBBS"
        experience = 2

    doctor = FakeDoctor()

    request = DoctorUpdateRequest(
        qualification="MD",
        experience=5,
    )

    response = {"updated": True}

    with patch.object(
        doctor_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        doctor_service.doctor_repo,
        "update",
        AsyncMock(),
    ) as update_mock, patch.object(
        doctor_service.DoctorMapper,
        "to_response",
        return_value=response,
    ):

        result = await doctor_service.update_doctor_profile(
            "user1",
            request,
        )

        update_mock.assert_awaited_once_with(doctor)

        assert doctor.qualification == "MD"
        assert doctor.experience == 5
        assert result == response


@pytest.mark.asyncio
async def test_update_doctor_profile_not_found():
    """Should raise when doctor does not exist."""

    request = DoctorUpdateRequest(
        qualification="MD",
    )

    with patch.object(
        doctor_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(DoctorNotFoundException):
            await doctor_service.update_doctor_profile(
                "user1",
                request,
            )


@pytest.mark.asyncio
async def test_get_doctor_by_user_id_success():
    """Should return doctor by user id."""

    doctor = object()
    response = {"id": "1"}

    with patch.object(
        doctor_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        doctor_service.DoctorMapper,
        "to_response",
        return_value=response,
    ):

        result = await doctor_service.get_doctor_by_user_id(
            "user1",
        )

        assert result == response


@pytest.mark.asyncio
async def test_get_doctor_by_user_id_not_found():
    """Should raise when doctor does not exist."""

    with patch.object(
        doctor_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(DoctorNotFoundException):
            await doctor_service.get_doctor_by_user_id(
                "user1",
            )