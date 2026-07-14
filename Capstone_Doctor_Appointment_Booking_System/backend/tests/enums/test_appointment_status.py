"""Tests for AppointmentStatus enum."""

from enums.appointment_status import AppointmentStatus


def test_appointment_status_values():
    """Verify all appointment status values."""

    assert AppointmentStatus.PENDING.value == "PENDING"
    assert AppointmentStatus.CONFIRMED.value == "CONFIRMED"
    assert AppointmentStatus.CANCELLED.value == "CANCELLED"
    assert AppointmentStatus.COMPLETED.value == "COMPLETED"
    assert AppointmentStatus.NO_SHOW.value == "NO_SHOW"


def test_appointment_status_is_string_enum():
    """Enum members should behave like strings."""

    assert isinstance(AppointmentStatus.PENDING.value, str)
    assert isinstance(AppointmentStatus.COMPLETED.value, str)


def test_total_appointment_status_members():
    """Ensure the enum contains exactly five members."""

    assert len(AppointmentStatus) == 5


def test_lookup_by_value():
    """Lookup enum using its string value."""

    assert AppointmentStatus("PENDING") == AppointmentStatus.PENDING
    assert AppointmentStatus("COMPLETED") == AppointmentStatus.COMPLETED
    assert AppointmentStatus("NO_SHOW") == AppointmentStatus.NO_SHOW


def test_lookup_by_name():
    """Lookup enum using its member name."""

    assert AppointmentStatus["PENDING"] == AppointmentStatus.PENDING
    assert AppointmentStatus["CONFIRMED"] == AppointmentStatus.CONFIRMED