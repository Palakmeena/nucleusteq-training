"""
Q17. Write a lambda function to find the square of a number.
"""

NUMBER = 5


def calculate_square(number: int) -> int:
    """
    Calculates the square of a number using a lambda function.
    """
    square_function = lambda value: value * value

    return square_function(number)


if __name__ == "__main__":
    print(calculate_square(NUMBER))