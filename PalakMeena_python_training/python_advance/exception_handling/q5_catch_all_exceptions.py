"""
Q5. Write a program that catches all exceptions and prints the error message.
"""

FIRST_NUMBER = 10
SECOND_NUMBER = 0


def catch_all_exceptions() -> None:
    """
    Demonstrates generic exception handling.
    """
    try:
        result = FIRST_NUMBER / SECOND_NUMBER
        print(result)

    except Exception as error:
        print(f"Error: {error}")


if __name__ == "__main__":
    catch_all_exceptions()