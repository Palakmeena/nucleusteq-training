"""
Q7. Check whether a number is even or odd.
Q8. Check whether a number is positive, negative, or zero.
Q9. Find the largest of three numbers.
Q10. Calculate grade based on marks.
Q11. Check whether a year is a leap year.
"""

def check_even_odd(number: int) -> str:
    """
    Q7. Check whether a number is even or odd.
    """
    if number % 2 == 0:
        return "Even"
    else:
        return "Odd"

def number_sign(number: int) -> str:
    """
    Q8. Check whether a number is positive, negative, or zero.
    """
    if number > 0:
        return "Positive"
    elif number < 0:
        return "Negative"
    else:
        return "Zero"

def largest_of_three(a: int, b: int, c: int) -> int:
    """
    Q9. Find the largest of three numbers.
    """
    return max(a, b, c)


def calculate_grade(marks: int) -> str:
    """
    Q10. Calculate grade based on marks.
    """
    if marks >= 90:
        return "A"
    elif marks >= 75:
        return "B"
    elif marks >= 50:
        return "C"
    else:
        return "Fail"


def is_leap_year(year: int) -> bool:
    """
    Q11. Check whether a year is a leap year.
    """
    return (year % 4 == 0 and year % 100 != 0) or (year % 400 == 0)


if __name__ == "__main__":
    print("Q7:", check_even_odd(10))
    print("Q8:", number_sign(-5))
    print("Q9:", largest_of_three(10, 25, 15))
    print("Q10:", calculate_grade(82))
    print("Q11:", is_leap_year(2024))