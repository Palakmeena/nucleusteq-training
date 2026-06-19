"""
Q14. Explain the difference between iterator and generator
with a small example.
"""

NUMBERS = [10, 20, 30]


def demonstrate_iterator() -> None:
    """
    Demonstrates the use of an iterator.
    """
    number_iterator = iter(NUMBERS)

    print("Iterator Output:")
    print(next(number_iterator))
    print(next(number_iterator))
    print(next(number_iterator))


def generate_numbers():
    """
    Demonstrates the use of a generator.
    """
    for number in NUMBERS:
        yield number


def demonstrate_generator() -> None:
    """
    Demonstrates the use of a generator.
    """
    print("\nGenerator Output:")

    for number in generate_numbers():
        print(number)


if __name__ == "__main__":
    demonstrate_iterator()
    demonstrate_generator()