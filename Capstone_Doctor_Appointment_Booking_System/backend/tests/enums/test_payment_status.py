"""Tests for payment status enum."""

from enums.payment_status import PaymentStatus


def test_payment_status_values():
    """Verify payment status values."""

    assert PaymentStatus.PENDING.value == "PENDING"
    assert PaymentStatus.PAID.value == "PAID"


def test_payment_status_member_count():
    """PaymentStatus should contain two members."""

    assert len(PaymentStatus) == 2


def test_payment_status_lookup():
    """Lookup PaymentStatus by value."""

    assert PaymentStatus("PENDING") == PaymentStatus.PENDING
    assert PaymentStatus("PAID") == PaymentStatus.PAID


def test_payment_status_is_string_enum():
    """Enum values should be strings."""

    assert isinstance(PaymentStatus.PENDING.value, str)
    assert isinstance(PaymentStatus.PAID.value, str)