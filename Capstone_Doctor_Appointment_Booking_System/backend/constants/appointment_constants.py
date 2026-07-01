"""Appointment-related constant messages."""

BOOKED_SUCCESS = "Appointment booked successfully"
CANCELLED_SUCCESS = "Appointment cancelled successfully"
APPOINTMENT_NOT_FOUND = "Appointment not found"
CANNOT_CANCEL = (
    "Appointment cannot be cancelled within 2 hours of scheduled time"
)
ALREADY_CANCELLED = "Appointment is already cancelled"
PAYMENT_SUCCESS = "Payment completed successfully"
STATUS_UPDATED = "Appointment status updated successfully"
APPOINTMENT_DATE_IN_PAST = "Appointment date cannot be in the past."
SLOT_DATE_MISMATCH = "Appointment date must match the slot date."
INVALID_SLOT = "Selected slot does not belong to the selected doctor."