"""
Q7. Create a custom exception called AgeException
and raise it if age is less than 18.
"""

MINIMUM_AGE = 18


class AgeException(Exception):
    """
    Custom exception raised when age is below the minimum age.
    """
    pass


def validate_age(age: int) -> None:
    """
    Validates age according to eligibility criteria.
    """
    if age < MINIMUM_AGE:
        raise AgeException(
            f"Age must be at least {MINIMUM_AGE} years."
        )

    print("Eligible.")


if __name__ == "__main__":
    try:
        validate_age(15)
    except AgeException as error:
        print(error)