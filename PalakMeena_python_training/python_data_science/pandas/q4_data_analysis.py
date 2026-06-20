"""
Q4. Data Analysis

Perform GroupBy operations on employee data.
"""

import pandas as pd


def create_employee_dataframe() -> pd.DataFrame:
    """
    Creates employee dataset.
    """
    employee_data = {
        "Name": ["Rahul", "Priya", "Amit", "Anuj"],
        "Age": [25, 30, 28, 35],
        "Department": ["HR", "IT", "Finance", "IT"],
        "Salary": [30000, 50000, 45000, 60000]
    }

    return pd.DataFrame(employee_data)


def display_average_salary_by_department(
    employee_df: pd.DataFrame
) -> None:
    """
    Displays average salary by department.
    """
    average_salary = (
        employee_df.groupby("Department")["Salary"]
        .mean()
    )

    print("\nAverage Salary By Department:")
    print(average_salary)


def display_max_salary_by_department(
    employee_df: pd.DataFrame
) -> None:
    """
    Displays maximum salary by department.
    """
    max_salary = (
        employee_df.groupby("Department")["Salary"]
        .max()
    )

    print("\nMaximum Salary By Department:")
    print(max_salary)


def display_employee_count_by_department(
    employee_df: pd.DataFrame
) -> None:
    """
    Displays employee count by department.
    """
    employee_count = (
        employee_df.groupby("Department")["Name"]
        .count()
    )

    print("\nEmployee Count By Department:")
    print(employee_count)


def main() -> None:
    """
    Main execution function.
    """
    employee_df = create_employee_dataframe()

    print("Employee Data:")
    print(employee_df)

    display_average_salary_by_department(
        employee_df
    )

    display_max_salary_by_department(
        employee_df
    )

    display_employee_count_by_department(
        employee_df
    )


if __name__ == "__main__":
    main()