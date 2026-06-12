import random


def random_numbers() -> None:
    """
    Q23. Generate random numbers using random module.
    """
    print("Random integer (1-100):", random.randint(1, 100))
    print("Random float (0-1):", random.random())