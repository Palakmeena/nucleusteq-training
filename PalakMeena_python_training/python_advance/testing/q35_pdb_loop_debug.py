"""
Q35. Use pdb breakpoints inside a loop
and inspect variable values.
"""

import pdb

NUMBERS = [10, 20, 30, 40, 50]


def print_running_total(numbers: list[int]) -> None:
    """
    Demonstrates debugging inside a loop.
    """

    total = 0

    for number in numbers:
        pdb.set_trace()

        total += number

        print(
            f"Current Number: {number}, "
            f"Running Total: {total}"
        )


if __name__ == "__main__":
    print_running_total(NUMBERS)