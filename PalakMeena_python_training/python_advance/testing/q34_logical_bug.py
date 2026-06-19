"""
Q34. Create a function with a logical bug and use pdb
to identify the issue.
"""

import pdb

NUMBERS = [10, 20, 30, 40]


def calculate_average(numbers: list[int]) -> float:
    """
    Contains an intentional logical bug for debugging.
    """

    pdb.set_trace()

    total = sum(numbers)

    # Intentional Bug:
    # Should be len(numbers)
    average = total / (len(numbers) - 1)

    return average


if __name__ == "__main__":
    print(calculate_average(NUMBERS))