"""
Pytest test cases for Q33.
"""

from q33_prime_function import is_prime


def test_prime_number() -> None:
    assert is_prime(13) is True


def test_non_prime_number() -> None:
    assert is_prime(12) is False


def test_one_is_not_prime() -> None:
    assert is_prime(1) is False