"""
Q5. Matplotlib Charts

Create different types of charts using Matplotlib.
"""

import os

import matplotlib.pyplot as plt


OUTPUT_DIRECTORY = "python_data_science/outputs"


def create_output_directory() -> None:
    """
    Creates output directory if it does not exist.
    """
    os.makedirs(
        OUTPUT_DIRECTORY,
        exist_ok=True
    )


def create_bar_chart() -> None:
    """
    Creates and saves a bar chart.
    """
    departments = ["HR", "IT", "Finance"]
    employees = [5, 12, 7]

    plt.figure(figsize=(6, 4))
    plt.bar(departments, employees)

    plt.title("Employees by Department")
    plt.xlabel("Department")
    plt.ylabel("Number of Employees")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/department_bar_chart.png"
    )

    plt.close()


def create_line_chart() -> None:
    """
    Creates and saves a line chart.
    """
    departments = ["HR", "IT", "Finance"]
    employees = [5, 12, 7]

    plt.figure(figsize=(6, 4))
    plt.plot(
        departments,
        employees,
        marker="o"
    )

    plt.title("Department Employee Trend")
    plt.xlabel("Department")
    plt.ylabel("Number of Employees")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/department_line_chart.png"
    )

    plt.close()


def create_histogram() -> None:
    """
    Creates and saves a histogram.
    """
    salaries = [
        30000,
        40000,
        50000,
        60000,
        45000
    ]

    plt.figure(figsize=(6, 4))
    plt.hist(salaries)

    plt.title("Salary Distribution")
    plt.xlabel("Salary")
    plt.ylabel("Frequency")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/salary_histogram.png"
    )

    plt.close()


def create_scatter_plot() -> None:
    """
    Creates and saves a scatter plot.
    """
    ages = [25, 30, 28, 35]
    salaries = [
        30000,
        50000,
        45000,
        60000
    ]

    plt.figure(figsize=(6, 4))
    plt.scatter(
        ages,
        salaries
    )

    plt.title("Age vs Salary")
    plt.xlabel("Age")
    plt.ylabel("Salary")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/age_salary_scatter.png"
    )

    plt.close()


def main() -> None:
    """
    Main execution function.
    """
    create_output_directory()

    create_bar_chart()
    create_line_chart()
    create_histogram()
    create_scatter_plot()

    print(
        "Charts generated successfully "
        "inside outputs folder."
    )


if __name__ == "__main__":
    main()