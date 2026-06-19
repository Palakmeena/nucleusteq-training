"""
Q15. Write a program that processes a large dataset using
a generator instead of storing all values in a list.
"""

DATASET_SIZE = 1_000_000


def generate_large_dataset(limit: int):
    """
    Generates numbers one at a time to save memory.
    """
    for number in range(1, limit + 1):
        yield number


def calculate_sum(limit: int) -> int:
    """
    Calculates the sum of a large dataset using a generator.
    """
    total = 0

    for number in generate_large_dataset(limit):
        total += number

    return total


if __name__ == "__main__":
    dataset_sum = calculate_sum(DATASET_SIZE)
    print(f"Sum: {dataset_sum}")