from enum import Enum


class AppointmentStatus(str, Enum):
    """Allowed appointment lifecycle states."""

    PENDING = "PENDING"
    CONFIRMED = "CONFIRMED"
    CANCELLED = "CANCELLED"
    COMPLETED = "COMPLETED"
    NO_SHOW = "NO_SHOW"