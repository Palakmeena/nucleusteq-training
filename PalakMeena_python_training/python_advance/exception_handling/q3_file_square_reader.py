"""
Exception Handling Questions
"""

FILE_NAME = "number.txt"


# Q3. Write a program using try-except-else-finally to read a number
# from a file and print its square.

def read_number_and_print_square(file_name: str) -> None:
    """
    Reads a number from a file and prints its square.
    Demonstrates try-except-else-finally.
    """
    try:
        with open(file_name, "r") as file:
            number = int(file.read().strip())

    except FileNotFoundError:
        print("Error: File not found.")

    except ValueError:
        print("Error: File does not contain a valid integer.")

    else:
        square = number * number
        print(f"Square: {square}")

    finally:
        print("File operation completed.")


if __name__ == "__main__":
    read_number_and_print_square(FILE_NAME)