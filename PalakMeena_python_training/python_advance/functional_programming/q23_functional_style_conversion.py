"""
Q23. Convert a simple loop-based program into a functional style
using map or filter.
"""

NUMBERS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]


def get_even_numbers(numbers: list[int]) -> list[int]:
    """
    Returns even numbers using filter() instead of a loop.
    """
    return list(
        filter(
            lambda number: number % 2 == 0,
            numbers
        )
    )


if __name__ == "__main__":
    print(get_even_numbers(NUMBERS))