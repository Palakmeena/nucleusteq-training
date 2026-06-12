"""
Q17. Calculate square of a number.
Q18. Check whether a string is palindrome.
Q19. Find maximum number from a list.
Q20. Function using default parameter.
"""


def square(number: int) -> int:
    """
    Q17. Calculate square of a number.
    """
    return number * number

def is_palindrome(value: str) -> bool:
    """
    Q18. Check whether a string is palindrome.
    """
    return value == value[::-1]

def find_maximum(numbers: list[int]) -> int:
    """
    Q19. Find maximum number from a list.
    """
    return max(numbers)


def greet(name: str = "Guest") -> str:
    """
    Q20. Function using default parameter.
    """
    return f"Hello {name}"


if __name__ == "__main__":
    print("Q17:", square(5))
    print("Q18:", is_palindrome("madam"))
    print("Q19:", find_maximum([10, 20, 5, 40]))
    print("Q20:", greet())
    print("Q20 (custom):", greet("Palak"))