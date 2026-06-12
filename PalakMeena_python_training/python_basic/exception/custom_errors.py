"""
Q33. Handle division with exception handling.
Q34. Custom exception for invalid age.
"""

def safe_division(a: int, b: int) -> float:
    """
    Q33. Handle division with exception handling.
    """
    try:
        result = a / b
        return result

    except ZeroDivisionError:
        print("Error: Cannot divide by zero")
        return 0



class AgeError(Exception):
    """
    Q34. Custom exception for invalid age.
    """
    pass


def check_age(age: int) -> str:
    """
    Raise custom exception if age is invalid.
    """
    if age < 0:
        raise AgeError("Age cannot be negative")

    return "Valid age"


if __name__ == "__main__":
    # Q33 test
    print("Q33:", safe_division(10, 2))
    print("Q33 (error case):", safe_division(10, 0))

    # Q34 test
    try:
        print("Q34:", check_age(20))
        print("Q34:", check_age(-5))
    except AgeError as e:
        print("Custom Error:", e)