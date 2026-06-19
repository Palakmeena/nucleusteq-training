"""
Q41. Create two threads that print numbers from 1 to 5.
"""

import threading


def print_numbers(thread_name: str) -> None:
    """
    Prints numbers from 1 to 5.
    """
    for number in range(1, 6):
        print(f"{thread_name}: {number}")


if __name__ == "__main__":
    thread_one = threading.Thread(
        target=print_numbers,
        args=("Thread-1",)
    )

    thread_two = threading.Thread(
        target=print_numbers,
        args=("Thread-2",)
    )

    thread_one.start()
    thread_two.start()

    thread_one.join()
    thread_two.join()