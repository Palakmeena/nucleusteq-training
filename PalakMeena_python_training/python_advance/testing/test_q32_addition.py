"""
Pytest test cases for Q32.
"""

from q32_addition_function import add_numbers


def test_add_positive_numbers() -> None:
    assert add_numbers(10, 20) == 30


def test_add_negative_numbers() -> None:
    assert add_numbers(-5, -5) == -10


def test_add_zero() -> None:
    assert add_numbers(10, 0) == 10