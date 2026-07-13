"""Doctor registration and availability status enums."""

from enum import Enum


class DoctorStatus(str, Enum):
    """Registration approval states for a doctor account."""

    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"


class DeactivationRequestStatus(str, Enum):
    """Lifecycle states for a doctor deactivation request."""

    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
