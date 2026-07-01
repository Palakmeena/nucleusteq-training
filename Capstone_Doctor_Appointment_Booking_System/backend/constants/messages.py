class AuthMessages:
    REGISTRATION_SUCCESS = "User registered successfully"
    LOGIN_SUCCESS = "Login successful"
    INVALID_CREDENTIALS = "Invalid email or password"
    EMAIL_ALREADY_EXISTS = "Email already registered"
    USER_NOT_FOUND = "User not found"
    ACCOUNT_INACTIVE = "Your account has been deactivated"
    UNAUTHORIZED = "Unauthorized access"
    FORBIDDEN = "You do not have permission to access this resource"
    INVALID_TOKEN = "Invalid or expired token"


class ValidationMessages:
    INVALID_EMAIL = "Invalid email format"
    INVALID_PHONE = "Phone must be exactly 10 digits"
    INVALID_PASSWORD = "Password must be 8-12 characters with uppercase and special character"
    INVALID_NAME = "Name must be at least 2 characters and alphabets only"


class TokenMessages:
    INVALID_TOKEN = "Invalid or expired token"
    TOKEN_EXPIRED = "Session expired, please login again"
    UNAUTHORIZED = "Unauthorized access"
    FORBIDDEN = "You do not have permission to access this resource"


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
    SLOT_OVERLAP = "Slot overlaps with an existing slot."
    INVALID_SLOT_TIME = "Start time must be earlier than end time."


class AppointmentMessages:
    BOOKED_SUCCESS = "Appointment booked successfully"
    CANCELLED_SUCCESS = "Appointment cancelled successfully"
    APPOINTMENT_NOT_FOUND = "Appointment not found"
    CANNOT_CANCEL = "Appointment cannot be cancelled within 2 hours of scheduled time"
    ALREADY_CANCELLED = "Appointment is already cancelled"
    PAYMENT_SUCCESS = "Payment completed successfully"
    STATUS_UPDATED = "Appointment status updated successfully"
    APPOINTMENT_DATE_IN_PAST = "Appointment date cannot be in the past."
    SLOT_DATE_MISMATCH = "Appointment date must match the slot date."
    INVALID_SLOT = "Selected slot does not belong to the selected doctor."