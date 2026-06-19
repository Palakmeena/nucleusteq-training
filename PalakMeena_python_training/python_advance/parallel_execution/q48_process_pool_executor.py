"""
Q48. Convert a normal function into parallel execution
using ProcessPoolExecutor.
"""

from concurrent.futures import ProcessPoolExecutor


def calculate_square(number: int) -> int:
    """
    Returns the square of a number.
    """
    return number * number


def main() -> None:
    """
    Executes tasks using ProcessPoolExecutor.
    """

    numbers = [1, 2, 3, 4, 5]

    with ProcessPoolExecutor(max_workers=3) as executor:
        results = executor.map(
            calculate_square,
            numbers
        )

    print(list(results))


if __name__ == "__main__":
    main()