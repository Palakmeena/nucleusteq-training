"""
Q9. Create an iterator for a list and print elements using next().
"""

NUMBERS = [10, 20, 30, 40, 50]


def print_elements_using_iterator(numbers: list[int]) -> None:
    """
    Creates an iterator from a list and prints elements using next().
    """
    number_iterator = iter(numbers)

    print(next(number_iterator))
    print(next(number_iterator))
    print(next(number_iterator))
    print(next(number_iterator))
    print(next(number_iterator))


if __name__ == "__main__":
    print_elements_using_iterator(NUMBERS)