"""
Q19. Use filter() to extract even numbers from a list.
"""

NUMBERS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]


def get_even_numbers(numbers: list[int]) -> list[int]:
    """
    Extracts even numbers from a list using filter().
    """
    return list(
        filter(
            lambda number: number % 2 == 0,
            numbers
        )
    )


if __name__ == "__main__":
    print(get_even_numbers(NUMBERS))