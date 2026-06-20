"""
Q1. NumPy Basics

Perform basic numerical operations using NumPy arrays.
"""

import numpy as np


def create_array() -> np.ndarray:
    """
    Creates and returns a NumPy array.
    """
    return np.array([10, 20, 30, 40, 50])


def display_array_statistics(numbers: np.ndarray) -> None:
    """
    Displays mean, maximum, minimum and sum.
    """
    print("Array:", numbers)
    print("Mean:", np.mean(numbers))
    print("Max:", np.max(numbers))
    print("Min:", np.min(numbers))
    print("Sum:", np.sum(numbers))


def perform_array_operations() -> None:
    """
    Performs addition and multiplication
    on two NumPy arrays.
    """
    arr_1 = np.array([1, 2, 3])
    arr_2 = np.array([4, 5, 6])

    print("\nArray 1:", arr_1)
    print("Array 2:", arr_2)

    print("Addition:", arr_1 + arr_2)
    print("Multiplication:", arr_1 * arr_2)


def create_matrix() -> np.ndarray:
    """
    Creates a 3x3 matrix.
    """
    return np.array(
        [
            [1, 2, 3],
            [4, 5, 6],
            [7, 8, 9]
        ]
    )


def main() -> None:
    """
    Main execution function.
    """
    number_array = create_array()

    display_array_statistics(number_array)

    perform_array_operations()

    matrix = create_matrix()

    print("\n3x3 Matrix:")
    print(matrix)


if __name__ == "__main__":
    main()