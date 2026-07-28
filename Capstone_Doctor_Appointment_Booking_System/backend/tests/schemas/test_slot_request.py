"""Tests for slot request schemas."""

from datetime import datetime, timedelta

import pytest
from pydantic import ValidationError

from schemas.request.slot_request import (
    SlotCreateRequest,
    SlotUpdateRequest,
)


def test_create_slot_valid():
    """Valid slot creation."""

    tomorrow = (
        datetime.utcnow() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    request = SlotCreateRequest(
        date=tomorrow,
        start_time="10:00",
        end_time="10:30",
    )

    assert request.date == tomorrow


def test_create_slot_invalid_date_format():
    """Invalid date format."""

    with pytest.raises(ValidationError):
        SlotCreateRequest(
            date="14/07/2026",
            start_time="10:00",
            end_time="10:30",
        )


def test_create_slot_date_in_past():
    """Past dates are not allowed."""

    yesterday = (
        datetime.utcnow() - timedelta(days=1)
    ).strftime("%Y-%m-%d")

    with pytest.raises(ValidationError):
        SlotCreateRequest(
            date=yesterday,
            start_time="10:00",
            end_time="10:30",
        )


def test_create_slot_invalid_start_time():
    """Invalid start time."""

    tomorrow = (
        datetime.utcnow() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    with pytest.raises(ValidationError):
        SlotCreateRequest(
            date=tomorrow,
            start_time="25:00",
            end_time="10:30",
        )


def test_create_slot_invalid_end_time():
    """Invalid end time."""

    tomorrow = (
        datetime.utcnow() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    with pytest.raises(ValidationError):
        SlotCreateRequest(
            date=tomorrow,
            start_time="10:00",
            end_time="99:30",
        )


def test_create_slot_today_with_future_time():
    """Today's future time should be accepted."""

    today = datetime.utcnow().strftime("%Y-%m-%d")

    future_time = (
        datetime.utcnow() + timedelta(hours=1)
    ).strftime("%H:%M")

    request = SlotCreateRequest(
        date=today,
        start_time=future_time,
        end_time=(
            datetime.utcnow() + timedelta(hours=2)
        ).strftime("%H:%M"),
    )

    assert request.start_time == future_time


def test_create_slot_today_with_past_time():
    """Today's past time should fail."""

    today = datetime.utcnow().strftime("%Y-%m-%d")

    past_time = (
        datetime.utcnow() - timedelta(hours=1)
    ).strftime("%H:%M")

    with pytest.raises(ValidationError):
        SlotCreateRequest(
            date=today,
            start_time=past_time,
            end_time="23:59",
        )


def test_update_slot_empty_request():
    """All update fields are optional."""

    request = SlotUpdateRequest()

    assert request.date is None
    assert request.start_time is None
    assert request.end_time is None


def test_update_slot_valid():
    """Valid update."""

    tomorrow = (
        datetime.utcnow() + timedelta(days=1)
    ).strftime("%Y-%m-%d")

    request = SlotUpdateRequest(
        date=tomorrow,
        start_time="11:00",
        end_time="11:30",
    )

    assert request.start_time == "11:00"


def test_update_slot_invalid_date():
    """Invalid update date."""

    with pytest.raises(ValidationError):
        SlotUpdateRequest(
            date="2026/07/14",
        )


def test_update_slot_invalid_time():
    """Invalid update time."""

    with pytest.raises(ValidationError):
        SlotUpdateRequest(
            start_time="40:00",
        )


def test_update_slot_past_date():
    """Past update date."""

    yesterday = (
        datetime.utcnow() - timedelta(days=1)
    ).strftime("%Y-%m-%d")

    with pytest.raises(ValidationError):
        SlotUpdateRequest(
            date=yesterday,
        )


def test_update_slot_today_with_future_time():
    """Today's future time should be accepted."""

    today = datetime.utcnow().strftime("%Y-%m-%d")

    future_time = (
        datetime.utcnow() + timedelta(hours=1)
    ).strftime("%H:%M")

    request = SlotUpdateRequest(
        date=today,
        start_time=future_time,
    )

    assert request.start_time == future_time


def test_update_slot_today_with_past_time():
    """Past time today should fail."""

    today = datetime.utcnow().strftime("%Y-%m-%d")

    past_time = (
        datetime.utcnow() - timedelta(hours=1)
    ).strftime("%H:%M")

    with pytest.raises(ValidationError):
        SlotUpdateRequest(
            date=today,
            start_time=past_time,
        )