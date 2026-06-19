"""
Q42. Create a thread that calculates
the sum of numbers from 1 to 100.
"""

import threading


def calculate_sum() -> None:
    """
    Calculates and prints sum.
    """
    total = sum(range(1, 101))
    print(f"Sum: {total}")


if __name__ == "__main__":
    sum_thread = threading.Thread(
        target=calculate_sum
    )

    sum_thread.start()
    sum_thread.join()