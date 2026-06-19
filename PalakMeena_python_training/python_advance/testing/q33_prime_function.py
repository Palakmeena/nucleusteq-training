"""
Q33. Write pytest test cases for a function
that checks whether a number is prime.
"""


def is_prime(number: int) -> bool:
    """
    Checks whether a number is prime.
    """
    if number <= 1:
        return False

    for divisor in range(2, int(number ** 0.5) + 1):
        if number % divisor == 0:
            return False

    return True