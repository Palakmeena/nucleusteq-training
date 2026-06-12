"""
Q35. Create a file and write your name into it.
Q36. Read a file and count words, lines, and characters.
Q37. Append data to existing file.
Q38. Copy content from one file to another.
Q39. Search a word in a file.
"""

def write_file() -> None:
    """
    Q35. Create a file and write your name into it.
    """
    file = open("student.txt", "w")
    file.write("My name is Palak")
    file.close()


def read_file_stats() -> None:
    """
    Q36. Read a file and count words, lines, and characters.
    """
    file = open("student.txt", "r")

    content = file.read()

    lines = content.split("\n")
    words = content.split()

    print("Lines:", len(lines))
    print("Words:", len(words))
    print("Characters:", len(content))

    file.close()

def append_file() -> None:
    """
    Q37. Append data to existing file.
    """
    file = open("student.txt", "a")
    file.write("\nAdded new line")
    file.close()


def copy_file() -> None:
    """
    Q38. Copy content from one file to another.
    """
    source = open("student.txt", "r")
    destination = open("copy.txt", "w")

    content = source.read()
    destination.write(content)

    source.close()
    destination.close()


def search_word(word: str) -> bool:
    """
    Q39. Search a word in a file.
    """
    file = open("student.txt", "r")

    content = file.read()

    file.close()

    return word in content


if __name__ == "__main__":
    write_file()
    append_file()
    read_file_stats()
    copy_file()

    result = search_word("Palak")
    print("Word found:", result)