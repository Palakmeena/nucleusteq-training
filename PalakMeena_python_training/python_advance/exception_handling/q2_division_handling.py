"""
Exception Handling Questions
"""

FIRST_NUMBER_PROMPT = "Enter first number: "
SECOND_NUMBER_PROMPT = "Enter second number: "


# Q2. Write a program to divide two numbers entered by the user
# and handle ZeroDivisionError.

def divide_numbers() -> None:
    """
    Divides two numbers and handles division by zero.
    """
    try:
        first_number = float(input(FIRST_NUMBER_PROMPT))
        second_number = float(input(SECOND_NUMBER_PROMPT))

        result = first_number / second_number

        print(f"Result: {result}")

    except ZeroDivisionError:
        print("Error: Division by zero is not allowed.")

    except ValueError:
        print("Error: Please enter valid numeric values.")


if __name__ == "__main__":
    divide_numbers()