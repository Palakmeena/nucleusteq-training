"""Tests for doctor status enums."""

from enums.doctor_status import (
    DoctorStatus,
    DeactivationRequestStatus,
)


def test_doctor_status_values():
    """Verify doctor status values."""

    assert DoctorStatus.PENDING.value == "PENDING"
    assert DoctorStatus.APPROVED.value == "APPROVED"
    assert DoctorStatus.REJECTED.value == "REJECTED"


def test_doctor_status_member_count():
    """DoctorStatus should contain three members."""

    assert len(DoctorStatus) == 3


def test_doctor_status_lookup():
    """Lookup DoctorStatus by value."""

    assert DoctorStatus("PENDING") == DoctorStatus.PENDING
    assert DoctorStatus("APPROVED") == DoctorStatus.APPROVED
    assert DoctorStatus("REJECTED") == DoctorStatus.REJECTED


def test_deactivation_request_status_values():
    """Verify deactivation request status values."""

    assert DeactivationRequestStatus.PENDING.value == "PENDING"
    assert DeactivationRequestStatus.APPROVED.value == "APPROVED"
    assert DeactivationRequestStatus.REJECTED.value == "REJECTED"


def test_deactivation_request_status_member_count():
    """DeactivationRequestStatus should contain three members."""

    assert len(DeactivationRequestStatus) == 3


def test_deactivation_request_status_lookup():
    """Lookup DeactivationRequestStatus by value."""

    assert (
        DeactivationRequestStatus("PENDING")
        == DeactivationRequestStatus.PENDING
    )
    assert (
        DeactivationRequestStatus("APPROVED")
        == DeactivationRequestStatus.APPROVED
    )
    assert (
        DeactivationRequestStatus("REJECTED")
        == DeactivationRequestStatus.REJECTED
    )