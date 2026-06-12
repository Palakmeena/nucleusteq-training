"""
Q1. Print Welcome message
Q2. Check Python version
Q3. Take user input and print message
"""

import sys


def welcome_message() -> None:
    print("Welcome to Python Training")


def check_python_version() -> None:
    print("Python Version:", sys.version)


def user_input_example() -> None:
    name: str = input("Enter your name: ")
    age: int = int(input("Enter your age: "))
    print(f"Hello {name}, you are {age} years old")


if __name__ == "__main__":
    print("Q1 Output:")
    welcome_message()

    print("\nQ2 Output:")
    check_python_version()

    print("\nQ3 Output:")
    user_input_example()