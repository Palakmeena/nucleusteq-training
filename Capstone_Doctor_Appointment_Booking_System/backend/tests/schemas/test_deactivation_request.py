"""Tests for deactivation request schemas."""

import pytest
from pydantic import ValidationError

from schemas.request.deactivation_request import (
    DeactivationRequestCreate,
)


def test_deactivation_request_valid():
    """Valid deactivation request."""

    request = DeactivationRequestCreate(
        start_date="2026-07-20",
        end_date="2026-07-25",
        reason="Medical leave",
    )

    assert request.start_date == "2026-07-20"
    assert request.end_date == "2026-07-25"
    assert request.reason == "Medical leave"


def test_deactivation_request_without_reason():
    """Reason is optional."""

    request = DeactivationRequestCreate(
        start_date="2026-07-20",
        end_date="2026-07-25",
    )

    assert request.reason is None


def test_invalid_start_date_format():
    """Invalid start date should fail."""

    with pytest.raises(ValidationError):
        DeactivationRequestCreate(
            start_date="20-07-2026",
            end_date="2026-07-25",
            reason="Medical leave",
        )


def test_invalid_end_date_format():
    """Invalid end date should fail."""

    with pytest.raises(ValidationError):
        DeactivationRequestCreate(
            start_date="2026-07-20",
            end_date="25-07-2026",
            reason="Medical leave",
        )


@pytest.mark.parametrize("invalid_date", ["2026-02-30", "2026-13-01"])
def test_invalid_calendar_date_is_rejected(invalid_date):
    """Dates with a valid shape but invalid calendar values should fail."""

    with pytest.raises(ValidationError):
        DeactivationRequestCreate(
            start_date=invalid_date,
            end_date="2026-07-25",
        )


def test_both_dates_invalid():
    """Both dates in wrong format should fail."""

    with pytest.raises(ValidationError):
        DeactivationRequestCreate(
            start_date="20/07/2026",
            end_date="25/07/2026",
        )


def test_empty_reason_is_allowed():
    """Empty string is still a valid optional reason."""

    request = DeactivationRequestCreate(
        start_date="2026-07-20",
        end_date="2026-07-25",
        reason="",
    )

    assert request.reason == ""
