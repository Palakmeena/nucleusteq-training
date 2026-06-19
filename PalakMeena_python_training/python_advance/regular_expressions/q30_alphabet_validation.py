"""
Q30. Write a pattern to check if a string contains only alphabets.
"""

import re

ALPHABET_PATTERN = r"^[A-Za-z]+$"


def contains_only_alphabets(text: str) -> bool:
    """
    Checks whether a string contains only alphabets.
    """
    return bool(re.match(ALPHABET_PATTERN, text))


if __name__ == "__main__":
    print(contains_only_alphabets("Python"))
    print(contains_only_alphabets("Python123"))