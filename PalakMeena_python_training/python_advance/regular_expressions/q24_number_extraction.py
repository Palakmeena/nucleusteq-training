"""
Q24. Write a program to extract all numbers from a given string
using regular expressions.
"""

import re

TEXT = "Palak scored 95 marks in Python and 88 marks in Java."


def extract_numbers(text: str) -> list[str]:
    """
    Extracts all numbers from a string using regular expressions.
    """
    return re.findall(r"\d+", text)


if __name__ == "__main__":
    print(extract_numbers(TEXT))