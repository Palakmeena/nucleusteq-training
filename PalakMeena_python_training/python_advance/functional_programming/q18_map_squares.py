"""
Q18. Use map() to convert a list of numbers into their squares.
"""

NUMBERS = [1, 2, 3, 4, 5]


def get_square_numbers(numbers: list[int]) -> list[int]:
    """
    Converts a list of numbers into their squares using map().
    """
    return list(map(lambda number: number * number, numbers))


if __name__ == "__main__":
    print(get_square_numbers(NUMBERS))