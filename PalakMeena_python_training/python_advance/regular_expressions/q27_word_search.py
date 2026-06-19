"""
Q27. Use re.search() to check whether a word exists in a sentence.
"""

import re

SENTENCE = "Python is a powerful programming language."
SEARCH_WORD = "powerful"


def word_exists(sentence: str, word: str) -> bool:
    """
    Checks whether a word exists in a sentence.
    """
    return re.search(word, sentence) is not None


if __name__ == "__main__":
    print(word_exists(SENTENCE, SEARCH_WORD))