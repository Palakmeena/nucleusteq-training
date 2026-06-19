"""
Q6. Create a function that raises a ValueError if a number is negative.
"""


def validate_positive_number(number: int) -> None:
    """
    Raises ValueError when a negative number is provided.
    """
    if number < 0:
        raise ValueError("Negative numbers are not allowed.")

    print("Valid number.")


if __name__ == "__main__":
    try:
        validate_positive_number(-5)
    except ValueError as error:
        print(error)