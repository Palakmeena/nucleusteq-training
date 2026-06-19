"""
Q40. Use the math package.
"""

from addition import add
from subtraction import subtract
from multiplication import multiply
from division import divide

FIRST_NUMBER = 20
SECOND_NUMBER = 10


def display_results() -> None:
    print(f"Addition: {add(FIRST_NUMBER, SECOND_NUMBER)}")
    print(f"Subtraction: {subtract(FIRST_NUMBER, SECOND_NUMBER)}")
    print(f"Multiplication: {multiply(FIRST_NUMBER, SECOND_NUMBER)}")
    print(f"Division: {divide(FIRST_NUMBER, SECOND_NUMBER)}")


if __name__ == "__main__":
    display_results()