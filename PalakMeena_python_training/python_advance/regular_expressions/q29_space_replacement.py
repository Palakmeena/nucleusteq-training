"""
Q29. Replace multiple spaces in a string with a single space using re.sub().
"""

import re

TEXT = "Python     is      easy      to      learn."


def remove_extra_spaces(text: str) -> str:
    """
    Replaces multiple spaces with a single space.
    """
    return re.sub(r"\s+", " ", text)


if __name__ == "__main__":
    print(remove_extra_spaces(TEXT))