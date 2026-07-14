"""Regression tests for appointment cancellation and slot reuse."""

from datetime import datetime, timedelta
from types import SimpleNamespace
from unittest.mock import AsyncMock, patch

import pytest

from enums.appointment_status import AppointmentStatus
from schemas.request.appointment_request import AppointmentBookRequest
from services import appointment_service


@pytest.mark.asyncio
async def test_cancelling_appointment_reopens_its_slot():
    """Cancellation keeps the record but makes its slot available."""

    appointment = SimpleNamespace(
        id="appointment-1",
        slot_id="slot-1",
        appointment_date=(datetime.utcnow() + timedelta(days=1)).strftime("%Y-%m-%d"),
        start_time="10:00",
        status=AppointmentStatus.CONFIRMED,
    )
    slot = SimpleNamespace(id="slot-1", is_booked=True)

    with patch.object(
        appointment_service.appointment_repo,
        "find_by_id_and_patient",
        AsyncMock(return_value=appointment),
    ), patch.object(
        appointment_service.slot_repo,
        "find_by_id",
        AsyncMock(return_value=slot),
    ), patch.object(
        appointment_service.slot_repo,
        "update",
        AsyncMock(),
    ) as update_slot, patch.object(
        appointment_service.appointment_repo,
        "update",
        AsyncMock(),
    ) as update_appointment:
        await appointment_service.cancel_appointment("appointment-1", "patient-a")

    assert slot.is_booked is False
    assert appointment.status == AppointmentStatus.CANCELLED
    update_slot.assert_awaited_once_with(slot)
    update_appointment.assert_awaited_once_with(appointment)


@pytest.mark.asyncio
async def test_another_patient_can_book_slot_after_cancellation():
    """A cancelled appointment is not treated as an active reservation."""

    appointment_date = (datetime.utcnow() + timedelta(days=1)).strftime("%Y-%m-%d")
    request = AppointmentBookRequest(
        doctor_id="doctor-1",
        slot_id="slot-1",
        appointment_date=appointment_date,
    )
    doctor = SimpleNamespace(is_active=True)
    slot = SimpleNamespace(
        doctor_id="doctor-1",
        date=appointment_date,
        start_time="10:00",
        end_time="10:30",
        is_booked=False,
    )
    new_appointment = SimpleNamespace(id="appointment-2")

    with patch.object(
        appointment_service.doctor_repo,
        "find_by_id",
        AsyncMock(return_value=doctor),
    ), patch.object(
        appointment_service.slot_repo,
        "find_by_id",
        AsyncMock(return_value=slot),
    ), patch.object(
        appointment_service.appointment_repo,
        "find_active_by_slot",
        AsyncMock(return_value=None),
    ) as find_active, patch.object(
        appointment_service.slot_repo,
        "update",
        AsyncMock(),
    ) as update_slot, patch.object(
        appointment_service.appointment_repo,
        "save",
        AsyncMock(),
    ) as save_appointment, patch.object(
        appointment_service,
        "Appointment",
        return_value=new_appointment,
    ), patch.object(
        appointment_service.AppointmentMapper,
        "to_response",
        return_value={"id": "appointment-2"},
    ):
        result = await appointment_service.book_appointment("patient-b", request)

    assert result == {"id": "appointment-2"}
    assert slot.is_booked is True
    find_active.assert_awaited_once_with("slot-1")
    update_slot.assert_awaited_once_with(slot)
    save_appointment.assert_awaited_once_with(new_appointment)