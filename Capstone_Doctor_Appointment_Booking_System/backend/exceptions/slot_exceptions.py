"""Custom slot exceptions."""


class SlotNotFoundException(Exception):
    """Raised when a slot cannot be found."""


class SlotAlreadyBookedException(Exception):
    """Raised when the slot has already been booked."""


class SlotOverlapException(Exception):
    """Raised when two slots overlap."""


class InvalidSlotTimeException(Exception):
    """Raised when slot timing is invalid."""


class SlotCannotBeDeletedException(Exception):
    """Raised when a slot cannot be deleted because it is booked."""