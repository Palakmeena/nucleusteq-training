"""Custom deactivation request exceptions."""


class DeactivationRequestNotFoundException(Exception):
    """Raised when a deactivation request cannot be found."""


class DeactivationRequestAlreadyProcessedException(Exception):
    """Raised when a deactivation request has already been approved or rejected."""


class InvalidDateRangeException(Exception):
    """Raised when the start date is after the end date."""
