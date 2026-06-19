"""
Exception Handling Questions
"""

INPUT_PROMPT = "Enter an integer: "


# Q1. Write a program that takes a number as input and handles ValueError
# if the input is not a valid integer.

def get_integer_input() -> None:
    """
    Reads an integer from the user and handles invalid input.
    """
    try:
        number = int(input(INPUT_PROMPT))
        print(f"Valid Integer: {number}")

    except ValueError:
        print("Error: Please enter a valid integer.")


if __name__ == "__main__":
    get_integer_input()