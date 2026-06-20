"""
Q6. Seaborn Visualizations

Create advanced visualizations using Seaborn.
"""

import os

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns


OUTPUT_DIRECTORY = "python_data_science/outputs"


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


def create_output_directory() -> None:
    """
    Creates output directory if it does not exist.
    """
    os.makedirs(
        OUTPUT_DIRECTORY,
        exist_ok=True
    )


def create_department_salary_barplot(
    employee_df: pd.DataFrame
) -> None:
    """
    Creates department vs salary barplot.
    """
    plt.figure(figsize=(6, 4))

    sns.barplot(
        data=employee_df,
        x="Department",
        y="Salary"
    )

    plt.title("Department vs Salary")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/department_salary_barplot.png"
    )

    plt.close()


def create_salary_boxplot(
    employee_df: pd.DataFrame
) -> None:
    """
    Creates salary distribution boxplot.
    """
    plt.figure(figsize=(6, 4))

    sns.boxplot(
        y=employee_df["Salary"]
    )

    plt.title("Salary Distribution")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/salary_boxplot.png"
    )

    plt.close()


def create_correlation_heatmap(
    employee_df: pd.DataFrame
) -> None:
    """
    Creates correlation heatmap.
    """
    plt.figure(figsize=(6, 4))

    correlation_matrix = employee_df[
        ["Age", "Salary"]
    ].corr()

    sns.heatmap(
        correlation_matrix,
        annot=True
    )

    plt.title("Age Salary Correlation")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/correlation_heatmap.png"
    )

    plt.close()


def main() -> None:
    """
    Main execution function.
    """
    create_output_directory()

    employee_df = create_employee_dataframe()

    create_department_salary_barplot(
        employee_df
    )

    create_salary_boxplot(
        employee_df
    )

    create_correlation_heatmap(
        employee_df
    )

    print(
        "Seaborn visualizations created successfully."
    )


if __name__ == "__main__":
    main()