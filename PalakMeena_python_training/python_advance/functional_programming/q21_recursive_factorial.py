"""
Q21. Write a recursive function to calculate factorial.
"""

NUMBER = 5


def calculate_factorial(number: int) -> int:
    """
    Calculates factorial using recursion.
    """
    if number <= 1:
        return 1

    return number * calculate_factorial(number - 1)


if __name__ == "__main__":
    print(calculate_factorial(NUMBER))