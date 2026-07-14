"""Tests for appointment request schemas."""

from datetime import datetime, timedelta

import pytest
from pydantic import ValidationError

from schemas.request.appointment_request import (
    AppointmentBookRequest,
    AppointmentStatusRequest,
)


def test_book_appointment_valid():
    """Valid appointment booking."""

    tomorrow = (
        datetime.utcnow() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    request = AppointmentBookRequest(
        doctor_id="doctor123",
        slot_id="slot123",
        appointment_date=tomorrow,
    )

    assert request.doctor_id == "doctor123"
    assert request.slot_id == "slot123"
    assert request.appointment_date == tomorrow


def test_book_appointment_invalid_date_format():
    """Appointment date must use YYYY-MM-DD."""

    with pytest.raises(ValidationError):
        AppointmentBookRequest(
            doctor_id="doctor123",
            slot_id="slot123",
            appointment_date="13/07/2026",
        )


def test_book_appointment_past_date():
    """Past appointment dates are not allowed."""

    yesterday = (
        datetime.utcnow() - timedelta(days=1)
    ).strftime("%Y-%m-%d")

    with pytest.raises(ValidationError):
        AppointmentBookRequest(
            doctor_id="doctor123",
            slot_id="slot123",
            appointment_date=yesterday,
        )


def test_status_request_completed():
    """Completed is a valid status."""

    request = AppointmentStatusRequest(
        status="COMPLETED",
    )

    assert request.status == "COMPLETED"


def test_status_request_no_show():
    """NO_SHOW is a valid status."""

    request = AppointmentStatusRequest(
        status="NO_SHOW",
    )

    assert request.status == "NO_SHOW"


def test_status_request_invalid():
    """Invalid appointment status should fail."""

    with pytest.raises(ValidationError):
        AppointmentStatusRequest(
            status="PENDING",
        )