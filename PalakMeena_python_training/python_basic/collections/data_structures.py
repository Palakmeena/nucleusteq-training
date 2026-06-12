"""
Q25. Create a list of 10 numbers and perform sum, max, sort, and remove duplicates.
Q26. Count even and odd numbers in a list.
Q27. Reverse a list without using reverse().
Q28. Create a tuple and access elements.
Q29. Convert tuple into list and modify it.
Q30. Perform union, intersection, and difference on two sets.
Q31. Remove duplicates from list using set.
Q32. Create a student dictionary and access values.
Q33. Count frequency of characters in a string using dictionary.
Q34. Merge two dictionaries.
"""

def list_operations() -> None:
    """
    Q25. Create a list of 10 numbers and perform sum, max, sort, and remove duplicates.
    """
    numbers: list[int] = [10, 20, 10, 5, 30, 40, 20, 50, 60, 5]

    print("Original List:", numbers)

    print("Sum:", sum(numbers))
    print("Max:", max(numbers))

    unique_numbers = list(set(numbers))
    print("Without duplicates:", unique_numbers)

    unique_numbers.sort()
    print("Sorted list:", unique_numbers)



def count_even_odd() -> None:
    """
    Q26. Count even and odd numbers in a list.
    """
    numbers: list[int] = [1, 2, 3, 4, 5, 6, 7, 8, 9]

    even_count = 0
    odd_count = 0

    for num in numbers:
        if num % 2 == 0:
            even_count += 1
        else:
            odd_count += 1

    print("Even:", even_count)
    print("Odd:", odd_count)


def reverse_list() -> None:
    """
    Q27. Reverse a list without using reverse().
    """
    numbers: list[int] = [1, 2, 3, 4, 5]

    reversed_list = []

    for i in range(len(numbers) - 1, -1, -1):
        reversed_list.append(numbers[i])

    print("Reversed List:", reversed_list)


def tuple_operations() -> None:
    """
    Q28. Create a tuple and access elements.
    """
    data: tuple[int, str, float] = (1, "Python", 10.5)

    print("First element:", data[0])
    print("Second element:", data[1])



def tuple_to_list() -> None:
    """
    Q29. Convert tuple into list and modify it.
    """
    data: tuple[int, int, int] = (10, 20, 30)

    data_list = list(data)
    data_list.append(40)

    print("Modified List:", data_list)


def set_operations() -> None:
    """
    Q30. Perform union, intersection, and difference on two sets.
    """
    set1: set[int] = {1, 2, 3, 4}
    set2: set[int] = {3, 4, 5, 6}

    print("Union:", set1 | set2)
    print("Intersection:", set1 & set2)
    print("Difference:", set1 - set2)


def remove_duplicates() -> None:
    """
    Q31. Remove duplicates from list using set.
    """
    numbers: list[int] = [1, 2, 2, 3, 4, 4, 5]

    unique_numbers = list(set(numbers))

    print("Without duplicates:", unique_numbers)


def student_dictionary() -> None:
    """
    Q32. Create a student dictionary and access values.
    """
    student: dict[str, str] = {
        "name": "Palak",
        "age": "21",
        "course": "Python"
    }

    print("Name:", student["name"])
    print("Age:", student["age"])
    print("Course:", student["course"])


def character_frequency() -> None:
    """
    Q33. Count frequency of characters in a string using dictionary.
    """
    text: str = "python"

    freq: dict[str, int] = {}

    for char in text:
        if char in freq:
            freq[char] += 1
        else:
            freq[char] = 1

    print("Frequency:", freq)


def merge_dictionaries() -> None:
    """
    Q34. Merge two dictionaries.
    """
    dict1: dict[str, int] = {"a": 1, "b": 2}
    dict2: dict[str, int] = {"c": 3, "d": 4}

    merged = {**dict1, **dict2}

    print("Merged Dictionary:", merged)



if __name__ == "__main__":
    list_operations()
    count_even_odd()
    reverse_list()
    tuple_operations()
    tuple_to_list()
    set_operations()
    remove_duplicates()
    student_dictionary()
    character_frequency()
    merge_dictionaries()