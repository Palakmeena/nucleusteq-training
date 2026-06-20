"""
Q2. Pandas DataFrame Creation

Create and manipulate an employee DataFrame.
"""

import pandas as pd


BONUS_PERCENTAGE = 0.10


def create_employee_dataframe() -> pd.DataFrame:
    """
    Creates and returns the employee DataFrame.
    """
    employee_data = {
        "Name": ["Rahul", "Priya", "Amit", "Anuj"],
        "Age": [25, 30, 28, 35],
        "Department": ["HR", "IT", "Finance", "IT"],
        "Salary": [30000, 50000, 45000, 60000]
    }

    return pd.DataFrame(employee_data)


def display_first_two_rows(employee_df: pd.DataFrame) -> None:
    """
    Displays first two rows.
    """
    print("\nFirst Two Rows:")
    print(employee_df.head(2))


def display_summary_statistics(employee_df: pd.DataFrame) -> None:
    """
    Displays summary statistics.
    """
    print("\nSummary Statistics:")
    print(employee_df.describe())


def display_it_employees(employee_df: pd.DataFrame) -> None:
    """
    Displays employees from IT department.
    """
    it_employees = employee_df[
        employee_df["Department"] == "IT"
    ]

    print("\nIT Employees:")
    print(it_employees)


def add_bonus_column(employee_df: pd.DataFrame) -> pd.DataFrame:
    """
    Adds bonus column to DataFrame.
    """
    employee_df["Bonus"] = (
        employee_df["Salary"] * BONUS_PERCENTAGE
    )

    return employee_df


def main() -> None:
    """
    Main execution function.
    """
    employee_df = create_employee_dataframe()

    print("Employee DataFrame:")
    print(employee_df)

    display_first_two_rows(employee_df)

    display_summary_statistics(employee_df)

    display_it_employees(employee_df)

    employee_df = add_bonus_column(employee_df)

    print("\nDataFrame With Bonus:")
    print(employee_df)


if __name__ == "__main__":
    main()