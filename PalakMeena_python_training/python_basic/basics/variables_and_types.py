"""
Q4. Create variables of type int, float, string, and boolean. Print their types.
Q5. Write a program to swap two numbers.
Q6. Take two numbers and print sum, difference, multiplication, and division.
"""

def show_data_types() -> None:
    """
    Q4. Create variables of type int, float, string, and boolean. Print their types.
    """
    a: int = 10
    b: float = 10.5
    c: str = "Hello"
    d: bool = True

    print(type(a))
    print(type(b))
    print(type(c))
    print(type(d))


def swap_numbers() -> None:
    """
    Q5. Write a program to swap two numbers.
    """
    a: int = 5
    b: int = 10

    a, b = b, a

    print("a =", a)
    print("b =", b)

def arithmetic_operations() -> None:
    """
    Q6. Take two numbers and print sum, difference, multiplication, and division.
    """
    a: int = 20
    b: int = 10

    print("Sum:", a + b)
    print("Difference:", a - b)
    print("Multiplication:", a * b)
    print("Division:", a / b)


if __name__ == "__main__":
    print("Q4 Output:")
    show_data_types()

    print("\nQ5 Output:")
    swap_numbers()

    print("\nQ6 Output:")
    arithmetic_operations()