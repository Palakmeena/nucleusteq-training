class ForbiddenException(Exception):
    """Raised when the user lacks permission."""


class InvalidTokenException(Exception):
    """Raised when the JWT token is invalid or expired."""


class InactiveAccountException(Exception):
    """Raised when the account is inactive."""


class EmailAlreadyExistsException(Exception):
    """Raised when an email is already registered."""


class UserNotFoundException(Exception):
    """Raised when the user cannot be found."""