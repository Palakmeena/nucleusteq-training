class AuthMessages:
    UNAUTHORIZED = "Unauthorized access"
    FORBIDDEN = "You do not have permission to access this resource"
    INVALID_TOKEN = "Invalid or expired token"


class DoctorMessages:
    PROFILE_CREATED = "Doctor profile created successfully"
    PROFILE_UPDATED = "Doctor profile updated successfully"
    DOCTOR_NOT_FOUND = "Doctor not found"
    PROFILE_ALREADY_EXISTS = "Doctor profile already exists"
    DOCTOR_ACTIVATED = "Doctor account activated successfully"
    DOCTOR_DEACTIVATED = "Doctor account deactivated successfully"


class SlotMessages:
    SLOT_CREATED = "Slot created successfully"
    SLOT_UPDATED = "Slot updated successfully"
    SLOT_DELETED = "Slot deleted successfully"
    SLOT_NOT_FOUND = "Slot not found"
    SLOT_ALREADY_BOOKED = "Slot is already booked"
    CANNOT_DELETE_BOOKED = "Cannot delete a booked slot"


class AppointmentMessages:
    BOOKED_SUCCESS = "Appointment booked successfully"
    CANCELLED_SUCCESS = "Appointment cancelled successfully"
    APPOINTMENT_NOT_FOUND = "Appointment not found"
    CANNOT_CANCEL = "Appointment cannot be cancelled within 2 hours of scheduled time"
    ALREADY_CANCELLED = "Appointment is already cancelled"
    PAYMENT_SUCCESS = "Payment completed successfully"
    STATUS_UPDATED = "Appointment status updated successfully"