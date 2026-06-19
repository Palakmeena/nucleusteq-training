"""
Q20. Use reduce() to find the product of all elements in a list.
"""

from functools import reduce

NUMBERS = [1, 2, 3, 4, 5]


def calculate_product(numbers: list[int]) -> int:
    """
    Calculates the product of all elements using reduce().
    """
    return reduce(
        lambda first_number, second_number:
        first_number * second_number,
        numbers
    )


if __name__ == "__main__":
    print(calculate_product(NUMBERS))