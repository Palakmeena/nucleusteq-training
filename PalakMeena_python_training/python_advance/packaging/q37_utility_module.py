"""
Q37. Import and use utility functions from another module.
"""

from q37_utility_module import (
    calculate_square,
    calculate_cube
)

NUMBER = 5


def display_results() -> None:
    """
    Displays square and cube values.
    """
    print(f"Square: {calculate_square(NUMBER)}")
    print(f"Cube: {calculate_cube(NUMBER)}")


if __name__ == "__main__":
    display_results()