"""
Q45. Write a program to create two processes
that print their Process IDs.
"""

from multiprocessing import Process
import os


def print_process_id() -> None:
    """
    Prints the current process ID.
    """
    print(f"Process ID: {os.getpid()}")


if __name__ == "__main__":
    process_one = Process(
        target=print_process_id
    )

    process_two = Process(
        target=print_process_id
    )

    process_one.start()
    process_two.start()

    process_one.join()
    process_two.join()