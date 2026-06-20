"""
Q3. Data Cleaning

Detect and handle missing values in a dataset.
"""

import pandas as pd


def create_employee_dataframe() -> pd.DataFrame:
    """
    Creates a DataFrame containing missing values.
    """
    employee_data = {
        "Name": ["Rahul", "Priya", "Anuj"],
        "Age": [25, None, 29],
        "Salary": [30000, 40000, None]
    }

    return pd.DataFrame(employee_data)


def display_missing_values(employee_df: pd.DataFrame) -> None:
    """
    Displays missing values in the DataFrame.
    """
    print("\nMissing Values:")
    print(employee_df.isnull())


def replace_missing_age(employee_df: pd.DataFrame) -> pd.DataFrame:
    """
    Replaces missing Age values with mean age.
    """
    mean_age = employee_df["Age"].mean()

    employee_df["Age"] = employee_df["Age"].fillna(mean_age)

    return employee_df


def replace_missing_salary(employee_df: pd.DataFrame) -> pd.DataFrame:
    """
    Replaces missing Salary values with 0.
    """
    employee_df["Salary"] = employee_df["Salary"].fillna(0)

    return employee_df


def main() -> None:
    """
    Main execution function.
    """
    employee_df = create_employee_dataframe()

    print("Original DataFrame:")
    print(employee_df)

    display_missing_values(employee_df)

    employee_df = replace_missing_age(employee_df)

    employee_df = replace_missing_salary(employee_df)

    print("\nCleaned DataFrame:")
    print(employee_df)


if __name__ == "__main__":
    main()