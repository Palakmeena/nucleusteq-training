"""
Q28. Use re.findall() to extract all words starting with a capital letter.
"""

import re

TEXT = "Palak is learning Python at NucleusTeq in Indore."


def extract_capital_words(text: str) -> list[str]:
    """
    Extracts all words that start with a capital letter.
    """
    return re.findall(r"\b[A-Z][a-zA-Z]*\b", text)


if __name__ == "__main__":
    print(extract_capital_words(TEXT))