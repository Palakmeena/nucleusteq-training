"""
Q25. Write a regular expression to validate an email address.
"""

import re

EMAIL_PATTERN = r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"


def is_valid_email(email: str) -> bool:
    """
    Validates an email address using a regular expression.
    """
    return bool(re.match(EMAIL_PATTERN, email))


if __name__ == "__main__":
    print(is_valid_email("palak@gmail.com"))
    print(is_valid_email("invalid-email"))