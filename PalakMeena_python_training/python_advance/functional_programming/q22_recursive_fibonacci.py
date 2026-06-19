"""
Q22. Write a recursive function to calculate Fibonacci.
"""

NUMBER = 8


def calculate_fibonacci(number: int) -> int:
    """
    Calculates Fibonacci number using recursion.
    """
    if number <= 1:
        return number

    return (
        calculate_fibonacci(number - 1)
        + calculate_fibonacci(number - 2)
    )


if __name__ == "__main__":
    print(calculate_fibonacci(NUMBER))