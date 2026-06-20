"""
Q7. Student Performance Analysis

Mini project using Pandas, Matplotlib and Seaborn.
"""

import os

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns


OUTPUT_DIRECTORY = "python_data_science/outputs"


def create_output_directory() -> None:
    """
    Creates output directory if it does not exist.
    """
    os.makedirs(
        OUTPUT_DIRECTORY,
        exist_ok=True
    )


def create_student_dataframe() -> pd.DataFrame:
    """
    Creates student dataset.
    """
    student_data = {
        "Name": ["Rahul", "Priya", "Siri", "Anuj"],
        "Marks": [70, 80, 90, 60],
        "Hours Studied": [2, 3, 5, 1]
    }

    return pd.DataFrame(student_data)


def add_performance_column(
    student_df: pd.DataFrame
) -> pd.DataFrame:
    """
    Adds performance column based on marks.
    """

    student_df["Performance"] = student_df[
        "Marks"
    ].apply(
        lambda marks: "Pass"
        if marks > 65
        else "Fail"
    )

    return student_df


def create_line_chart(
    student_df: pd.DataFrame
) -> None:
    """
    Creates Hours Studied vs Marks line chart.
    """

    plt.figure(figsize=(6, 4))

    plt.plot(
        student_df["Hours Studied"],
        student_df["Marks"],
        marker="o"
    )

    plt.title("Hours Studied vs Marks")
    plt.xlabel("Hours Studied")
    plt.ylabel("Marks")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/hours_vs_marks_line_chart.png"
    )

    plt.close()


def create_scatter_plot(
    student_df: pd.DataFrame
) -> None:
    """
    Creates Hours Studied vs Marks scatter plot.
    """

    plt.figure(figsize=(6, 4))

    plt.scatter(
        student_df["Hours Studied"],
        student_df["Marks"]
    )

    plt.title("Study Hours vs Marks")
    plt.xlabel("Hours Studied")
    plt.ylabel("Marks")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/study_vs_marks_scatter_plot.png"
    )

    plt.close()


def create_performance_barplot(
    student_df: pd.DataFrame
) -> None:
    """
    Creates performance vs marks barplot.
    """

    plt.figure(figsize=(6, 4))

    sns.barplot(
        data=student_df,
        x="Performance",
        y="Marks"
    )

    plt.title("Performance vs Marks")

    plt.savefig(
        f"{OUTPUT_DIRECTORY}/performance_vs_marks_barplot.png"
    )

    plt.close()


def main() -> None:
    """
    Main execution function.
    """

    create_output_directory()

    student_df = create_student_dataframe()

    student_df = add_performance_column(
        student_df
    )

    print("Student Data:")
    print(student_df)

    create_line_chart(student_df)

    create_scatter_plot(student_df)

    create_performance_barplot(student_df)

    print(
        "\nMini project completed successfully."
    )


if __name__ == "__main__":
    main()