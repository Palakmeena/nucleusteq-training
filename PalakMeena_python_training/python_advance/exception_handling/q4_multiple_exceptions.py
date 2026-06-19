"""
Q4. Handle multiple exceptions in a single program.
"""

FIRST_NUMBER = 10
SECOND_NUMBER = 0
INVALID_VALUE = "abc"


def handle_multiple_exceptions() -> None:
    """
    Demonstrates handling multiple exceptions in one program.
    """
    try:
        number = int(INVALID_VALUE)
        result = FIRST_NUMBER / SECOND_NUMBER

        print(number)
        print(result)

    except ValueError:
        print("ValueError: Invalid integer value.")

    except ZeroDivisionError:
        print("ZeroDivisionError: Cannot divide by zero.")


if __name__ == "__main__":
    handle_multiple_exceptions()