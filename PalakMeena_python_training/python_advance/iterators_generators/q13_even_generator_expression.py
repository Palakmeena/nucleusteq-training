"""
Q13. Write a generator expression to generate even numbers from 1 to 50.
"""

START_NUMBER = 1
END_NUMBER = 50


def print_even_numbers() -> None:
    """
    Prints even numbers using a generator expression.
    """
    even_numbers = (
        number
        for number in range(START_NUMBER, END_NUMBER + 1)
        if number % 2 == 0
    )

    for number in even_numbers:
        print(number)


if __name__ == "__main__":
    print_even_numbers()