from enum import Enum


class PaymentStatus(str, Enum):
    """Allowed payment states."""

    PENDING = "PENDING"
    PAID = "PAID"
