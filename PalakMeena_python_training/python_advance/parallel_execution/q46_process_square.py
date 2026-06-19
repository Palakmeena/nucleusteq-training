"""
Q46. Calculate squares using
the multiprocessing Process class.
"""

from multiprocessing import Process


def calculate_square(number: int) -> None:
    """
    Calculates and prints square.
    """
    print(
        f"Square of {number}: "
        f"{number * number}"
    )


if __name__ == "__main__":
    process_one = Process(
        target=calculate_square,
        args=(5,)
    )

    process_two = Process(
        target=calculate_square,
        args=(10,)
    )

    process_one.start()
    process_two.start()

    process_one.join()
    process_two.join()