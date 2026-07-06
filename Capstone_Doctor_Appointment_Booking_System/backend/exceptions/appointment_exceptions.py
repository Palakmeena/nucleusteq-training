"""Custom appointment exceptions."""


class AppointmentNotFoundException(Exception):
    """Raised when an appointment cannot be found."""


class AppointmentAlreadyCancelledException(Exception):
    """Raised when the appointment is already cancelled."""


class AppointmentCancellationException(Exception):
    """Raised when appointment cancellation is not allowed."""


class InvalidAppointmentStatusException(Exception):
    """Raised when the appointment status update is invalid."""