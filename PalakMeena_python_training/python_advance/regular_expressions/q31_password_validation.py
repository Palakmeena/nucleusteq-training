"""
Q31. Create a password validation program using regex
(minimum length, one digit, one special character).
"""

import re

PASSWORD_PATTERN = (
    r"^(?=.*[0-9])"
    r"(?=.*[!@#$%^&*(),.?\":{}|<>])"
    r".{8,}$"
)


def is_valid_password(password: str) -> bool:
    """
    Validates password based on length,
    digit requirement, and special character requirement.
    """
    return bool(re.match(PASSWORD_PATTERN, password))


if __name__ == "__main__":
    print(is_valid_password("Palak@123"))
    print(is_valid_password("palak123"))