"""Tests for slot service (Part 1)."""

from datetime import datetime, timedelta
from unittest.mock import AsyncMock, patch

import pytest
from pydantic import ValidationError

from exceptions.doctor_exceptions import DoctorNotFoundException
from exceptions.slot_exceptions import (
    InvalidSlotTimeException,
    SlotOverlapException,
)
from schemas.request.slot_request import SlotCreateRequest
from services import slot_service


# ---------------------------------------------------------------------
# is_overlapping()
# ---------------------------------------------------------------------


def test_is_overlapping_true():
    """Overlapping intervals."""

    assert slot_service.is_overlapping(
        "10:00",
        "11:00",
        "10:30",
        "11:30",
    )


def test_is_overlapping_false():
    """Non-overlapping intervals."""

    assert (
        slot_service.is_overlapping(
            "10:00",
            "11:00",
            "11:00",
            "12:00",
        )
        is False
    )


# ---------------------------------------------------------------------
# create_slot()
# ---------------------------------------------------------------------


@pytest.mark.asyncio
async def test_create_slot_success():
    """Create slot successfully."""

    tomorrow = (
        datetime.now() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    request = SlotCreateRequest(
        date=tomorrow,
        start_time="10:00",
        end_time="11:00",
    )

    fake_doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    fake_slot = type(
        "Slot",
        (),
        {
            "id": "slot1",
            "doctor_id": "doctor1",
            "date": tomorrow,
            "start_time": "10:00",
            "end_time": "11:00",
            "is_booked": False,
        },
    )()

    response = {"slot": True}

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=fake_doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_doctor_and_date",
        AsyncMock(return_value=[]),
    ), patch.object(
        slot_service.slot_repo,
        "save",
        AsyncMock(),
    ), patch.object(
        slot_service,
        "Slot",
        return_value=fake_slot,
    ), patch.object(
        slot_service.SlotMapper,
        "to_response",
        return_value=response,
    ):

        result = await slot_service.create_slot(
            "user1",
            request,
        )

    assert result == response


@pytest.mark.asyncio
async def test_create_slot_doctor_not_found():
    """Doctor doesn't exist."""

    tomorrow = (
        datetime.now() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    request = SlotCreateRequest(
        date=tomorrow,
        start_time="10:00",
        end_time="11:00",
    )

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(
            DoctorNotFoundException,
        ):
            await slot_service.create_slot(
                "user1",
                request,
            )


@pytest.mark.asyncio
async def test_create_slot_invalid_time():
    """Start time >= end time."""

    tomorrow = (
        datetime.now() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    request = SlotCreateRequest(
        date=tomorrow,
        start_time="11:00",
        end_time="10:00",
    )

    fake_doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=fake_doctor),
    ):

        with pytest.raises(
            InvalidSlotTimeException,
        ):
            await slot_service.create_slot(
                "user1",
                request,
            )


def test_create_slot_in_past_is_rejected_by_request_validation():
    """Creating a same-day slot in the past is rejected before service execution."""

    now = datetime.utcnow()
    today = now.strftime("%Y-%m-%d")
    past_time = (now - timedelta(minutes=1)).strftime("%H:%M")

    with pytest.raises(ValidationError, match="Time cannot be in the past"):
        SlotCreateRequest(
            date=today,
            start_time=past_time,
            end_time=past_time,
        )


@pytest.mark.asyncio
async def test_create_slot_overlap():
    """Overlapping slot."""

    tomorrow = (
        datetime.now() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    request = SlotCreateRequest(
        date=tomorrow,
        start_time="10:30",
        end_time="11:30",
    )

    fake_doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    existing_slot = type(
        "Slot",
        (),
        {
            "start_time": "10:00",
            "end_time": "11:00",
        },
    )()

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=fake_doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_doctor_and_date",
        AsyncMock(return_value=[existing_slot]),
    ):

        with pytest.raises(
            SlotOverlapException,
        ):
            await slot_service.create_slot(
                "user1",
                request,
            )


# ---------------------------------------------------------------------
# update_slot()
# ---------------------------------------------------------------------

from exceptions.slot_exceptions import (
    SlotAlreadyBookedException,
    SlotCannotBeDeletedException,
    SlotNotFoundException,
)


@pytest.mark.asyncio
async def test_update_slot_success():
    """Update slot successfully."""

    tomorrow = (
        datetime.now() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    slot = type(
        "Slot",
        (),
        {
            "id": "slot1",
            "date": tomorrow,
            "start_time": "10:00",
            "end_time": "11:00",
            "is_booked": False,
        },
    )()

    request = slot_service.SlotUpdateRequest(
        start_time="11:00",
        end_time="12:00",
    )

    response = {"updated": True}

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_id_and_doctor",
        AsyncMock(return_value=slot),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_doctor_and_date",
        AsyncMock(return_value=[]),
    ), patch.object(
        slot_service.slot_repo,
        "update",
        AsyncMock(),
    ) as update_mock, patch.object(
        slot_service.SlotMapper,
        "to_response",
        return_value=response,
    ):

        result = await slot_service.update_slot(
            "user1",
            "slot1",
            request,
        )

    update_mock.assert_awaited_once()
    assert result == response


@pytest.mark.asyncio
async def test_update_slot_not_found():
    """Slot not found."""

    doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    request = slot_service.SlotUpdateRequest()

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_id_and_doctor",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(
            SlotNotFoundException,
        ):
            await slot_service.update_slot(
                "user1",
                "slot1",
                request,
            )


@pytest.mark.asyncio
async def test_update_slot_booked():
    """Booked slot cannot be updated."""

    doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    slot = type(
        "Slot",
        (),
        {
            "is_booked": True,
        },
    )()

    request = slot_service.SlotUpdateRequest()

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_id_and_doctor",
        AsyncMock(return_value=slot),
    ):

        with pytest.raises(
            SlotAlreadyBookedException,
        ):
            await slot_service.update_slot(
                "user1",
                "slot1",
                request,
            )


# ---------------------------------------------------------------------
# delete_slot()
# ---------------------------------------------------------------------


@pytest.mark.asyncio
async def test_delete_slot_success():
    """Delete slot successfully."""

    doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    slot = type(
        "Slot",
        (),
        {
            "id": "slot1",
            "is_booked": False,
        },
    )()

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_id_and_doctor",
        AsyncMock(return_value=slot),
    ), patch.object(
        slot_service.slot_repo,
        "delete",
        AsyncMock(),
    ) as delete_mock:

        result = await slot_service.delete_slot(
            "user1",
            "slot1",
        )

    delete_mock.assert_awaited_once()
    assert "message" in result


@pytest.mark.asyncio
async def test_delete_slot_not_found():
    """Delete missing slot."""

    doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_id_and_doctor",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(
            SlotNotFoundException,
        ):
            await slot_service.delete_slot(
                "user1",
                "slot1",
            )


@pytest.mark.asyncio
async def test_delete_slot_booked():
    """Booked slot cannot be deleted."""

    doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    slot = type(
        "Slot",
        (),
        {
            "is_booked": True,
        },
    )()

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_by_id_and_doctor",
        AsyncMock(return_value=slot),
    ):

        with pytest.raises(
            SlotCannotBeDeletedException,
        ):
            await slot_service.delete_slot(
                "user1",
                "slot1",
            )


# ---------------------------------------------------------------------
# get_slots_by_doctor()
# ---------------------------------------------------------------------


@pytest.mark.asyncio
async def test_get_slots_by_doctor():
    """Get doctor's slots."""

    slot = object()

    response = {"slot": 1}

    with patch.object(
        slot_service.slot_repo,
        "find_all_by_doctor",
        AsyncMock(return_value=[slot]),
    ), patch.object(
        slot_service.SlotMapper,
        "to_response",
        return_value=response,
    ):

        result = await slot_service.get_slots_by_doctor(
            "doctor1",
        )

    assert result == [response]


# ---------------------------------------------------------------------
# get_my_slots()
# ---------------------------------------------------------------------


@pytest.mark.asyncio
async def test_get_my_slots_success():
    """Get current doctor's slots."""

    doctor = type(
        "Doctor",
        (),
        {
            "id": "doctor1",
        },
    )()

    slot = object()

    response = {"slot": 1}

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        slot_service.slot_repo,
        "find_all_by_doctor",
        AsyncMock(return_value=[slot]),
    ), patch.object(
        slot_service.SlotMapper,
        "to_response",
        return_value=response,
    ):

        result = await slot_service.get_my_slots(
            "user1",
        )

    assert result == [response]


@pytest.mark.asyncio
async def test_get_my_slots_doctor_not_found():
    """Doctor not found."""

    with patch.object(
        slot_service.doctor_repo,
        "find_by_user_id",
        AsyncMock(return_value=None),
    ):

        with pytest.raises(
            DoctorNotFoundException,
        ):
            await slot_service.get_my_slots(
                "user1",
            )
