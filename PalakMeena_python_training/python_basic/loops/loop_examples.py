"""
Q12. Print numbers from 1 to 100 using loop.
Q13. Print multiplication table of a number.
Q14. Find factorial of a number.
Q15. Reverse a number using loop.
Q16. Check whether a number is prime.
"""


def print_numbers_1_to_100() -> None:
    """
    Q12. Print numbers from 1 to 100 using loop
    """
    for i in range(1, 101):
        print(i)


def multiplication_table(number: int) -> None:
    """
    Q13. Print multiplication table of a number
    """
    for i in range(1, 11):
        print(f"{number} x {i} = {number * i}")


def factorial(number: int) -> int:
    """
    Q14. Find factorial of a number
    """
    result = 1
    for i in range(1, number + 1):
        result *= i
    return result


def reverse_number(number: int) -> int:
    """
    Q15. Reverse a number using loop
    """
    reversed_num = 0

    while number > 0:
        digit = number % 10
        reversed_num = reversed_num * 10 + digit
        number //= 10

    return reversed_num


def is_prime(number: int) -> bool:
    """
    Q16. Check whether a number is prime
    """
    if number <= 1:
        return False

    for i in range(2, int(number ** 0.5) + 1):
        if number % i == 0:
            return False

    return True


if __name__ == "__main__":
    print("Q12 Output:")
    print_numbers_1_to_100()

    print("\nQ13 Output:")
    multiplication_table(5)

    print("\nQ14 Output:", factorial(5))

    print("\nQ15 Output:", reverse_number(1234))

    print("\nQ16 Output:", is_prime(7))