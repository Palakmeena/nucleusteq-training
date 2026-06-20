# Python Data Science and Visualization Assignment

## Overview
This project contains solutions for the Python Data Science and Visualization assignment. The assignment focuses on practical usage of NumPy, Pandas, Matplotlib, and Seaborn for data manipulation, analysis, cleaning, and visualization.

The project follows Python best practices including:

- Modular project structure
- Meaningful naming conventions
- Type hints
- Function-level documentation (docstrings)
- PEP 8 coding standards
- Reusable functions
- Separation of concerns

---

## Technologies Used

- Python 3
- NumPy
- Pandas
- Matplotlib
- Seaborn

---

## Project Structure

```
python_data_science/
│
├── numpy/
│   └── q1_numpy_basics.py
│
├── pandas/
│   ├── q2_dataframe_creation.py
│   ├── q3_data_cleaning.py
│   └── q4_data_analysis.py
│
├── matplotlib/
│   └── q5_matplotlib_charts.py
│
├── seaborn/
│   └── q6_seaborn_visualizations.py
│
├── mini_project/
│   └── q7_student_performance_analysis.py
│
├── outputs/
│   ├── Generated chart images
│
└── README.md
```

---

# Assignment 1: NumPy Basics

## Objectives

- Create NumPy arrays
- Perform mathematical operations
- Work with matrices

## Features Implemented

- Array creation
- Mean calculation
- Maximum value calculation
- Minimum value calculation
- Sum calculation
- Array addition
- Array multiplication
- 3 × 3 matrix creation

---

# Assignment 2: Pandas DataFrame Creation

## Objectives
Learn DataFrame creation and manipulation.

## Features Implemented

- Employee DataFrame creation
- Display first two rows
- Summary statistics generation
- Filtering employees by department
- Bonus column calculation

## Dataset
Contains:

- Name
- Age
- Department
- Salary

---

# Assignment 3: Data Cleaning

## Objectives
Handle missing data using Pandas.

## Features Implemented

- Missing value detection
- Age replacement using mean value
- Salary replacement using default value (0)

## Concepts Covered

- isnull()
- fillna()
- Mean calculation

---

# Assignment 4: Data Analysis

## Objectives
Perform analytical operations using GroupBy.

## Features Implemented

- Average salary by department
- Maximum salary by department
- Employee count by department

## Concepts Covered

- groupby()
- mean()
- max()
- count()

---

# Assignment 5: Matplotlib Charts

## Objectives
Create basic visualizations using Matplotlib.

## Charts Generated

### Bar Chart
Employees by Department

### Line Chart
Department Employee Trend

### Histogram
Salary Distribution

### Scatter Plot
Age vs Salary

## Output
Generated chart images are stored inside the outputs directory.

---

# Assignment 6: Seaborn Visualizations

## Objectives
Create advanced visualizations using Seaborn.

## Charts Generated

### Barplot
Department vs Salary

### Boxplot
Salary Distribution Analysis

### Heatmap
Correlation between Age and Salary

## Output
Generated chart images are stored inside the outputs directory.

---

# Assignment 7: Student Performance Analysis Mini Project

## Objectives
Perform end-to-end data analysis and visualization.

## Dataset
Student information including:

- Name
- Marks
- Hours Studied

## Features Implemented

### Data Processing

- Student DataFrame creation
- Performance classification
- Pass/Fail categorization

### Visualizations

- Hours Studied vs Marks Line Chart
- Study Hours vs Marks Scatter Plot
- Performance vs Marks Barplot

## Performance Logic
Pass:
Marks > 65

Fail:
Marks ≤ 65

---

# Outputs
All generated visualizations are automatically saved inside:

```
python_data_science/outputs/
```
Example output files:

- department_bar_chart.png
- department_line_chart.png
- salary_histogram.png
- age_salary_scatter.png
- department_salary_barplot.png
- salary_boxplot.png
- correlation_heatmap.png
- hours_vs_marks_line_chart.png
- study_vs_marks_scatter_plot.png
- performance_vs_marks_barplot.png

---

# Learning Outcomes
Through this assignment, the following concepts were practiced:

- NumPy array operations
- Pandas DataFrame manipulation
- Data cleaning techniques
- Data analysis using GroupBy
- Data visualization using Matplotlib
- Advanced visualization using Seaborn
- Real-world mini project implementation
- Python coding standards and modular design

---

# Author
Palak Meena

Python Training Assignment – Data Science and Visualization

