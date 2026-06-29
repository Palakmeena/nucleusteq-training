class AuthMessages:
    REGISTRATION_SUCCESS = "User registered successfully"
    LOGIN_SUCCESS = "Login successful"
    INVALID_CREDENTIALS = "Invalid email or password"
    EMAIL_ALREADY_EXISTS = "Email already registered"
    USER_NOT_FOUND = "User not found"
    ACCOUNT_INACTIVE = "Your account has been deactivated"


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