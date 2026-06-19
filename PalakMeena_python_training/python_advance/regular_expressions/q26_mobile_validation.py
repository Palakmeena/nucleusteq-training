"""
Q26. Write a regular expression to validate a 10-digit mobile number.
"""

import re

MOBILE_PATTERN = r"^[0-9]{10}$"


def is_valid_mobile_number(mobile_number: str) -> bool:
    """
    Validates a 10-digit mobile number.
    """
    return bool(re.match(MOBILE_PATTERN, mobile_number))


if __name__ == "__main__":
    print(is_valid_mobile_number("9876543210"))
    print(is_valid_mobile_number("12345"))