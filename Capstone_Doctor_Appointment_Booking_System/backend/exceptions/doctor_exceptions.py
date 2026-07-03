"""Custom doctor exceptions."""


class DoctorNotFoundException(Exception):
    """Raised when a doctor cannot be found."""


class DoctorInactiveException(Exception):
    """Raised when the doctor account is inactive."""


class DoctorProfileAlreadyExistsException(Exception):
    """Raised when a doctor profile already exists."""