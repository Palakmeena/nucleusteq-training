"""
Q8. Write a program that handles FileNotFoundError
when trying to open a file.
"""


def open_file(file_name: str) -> None:
    """
    Opens a file and handles FileNotFoundError.
    """
    try:
        with open(file_name, "r") as file:
            print(file.read())

    except FileNotFoundError:
        print("Error: File not found.")


if __name__ == "__main__":
    open_file("sample.txt")