# PalakMeena Python Training

This repository contains Python training exercises organized into small, focused modules. The structure follows the assignment requirement of separating beginner topics from advanced topics and keeping each topic in its own package.

## Repository Layout

```text
PalakMeena_python_training/
├── .gitignore
├── requirements.txt
├── __init__.py
├── python_basic/
│   ├── __init__.py
│   ├── basics/
│   │   ├── __init__.py
│   │   └── variables_and_types.py
│   ├── conditionals/
│   │   ├── __init__.py
│   │   └── conditional_examples.py
│   ├── collections/
│   │   ├── __init__.py
│   │   └── data_structures.py
│   ├── exception/
│   │   ├── __init__.py
│   │   └── custom_errors.py
│   ├── file_handling/
│   │   ├── __init__.py
│   │   └── file_operations.py
│   ├── functions/
│   │   ├── __init__.py
│   │   └── utility_functions.py
│   ├── introduction/
│   │   ├── __init__.py
│   │   └── introduction_examples.py
│   ├── loops/
│   │   ├── __init__.py
│   │   └── loop_examples.py
│   └── modules/
│       ├── __init__.py
│       ├── custom_module.py
│       ├── math_examples.py
│       └── random_examples.py
└── python_advance/
    ├── __init__.py
    └── oop/
        ├── __init__.py
        └── models.py
```

## What Each Folder Is For

### `python_basic/`
Beginner-level Python exercises and practice files.

- `introduction/` - basic output, version checking, and user input examples
- `basics/` - variables, data types, and simple operations
- `conditionals/` - if/elif/else practice
- `loops/` - for/while loops and repetition tasks
- `functions/` - reusable functions and parameter handling
- `modules/` - built-in module usage and custom module import examples
- `collections/` - list, tuple, set, and dictionary exercises
- `exception/` - custom error handling practice
- `file_handling/` - file creation, reading, writing, and search exercises

### `python_advance/`
Advanced Python practice.

- `oop/` - object-oriented programming examples and model classes

## Assignment Coverage

The assignment asks for exercises covering:

1. Introduction to Python
2. Variables and data types
3. Operators and conditionals
4. Loops
5. Functions
6. Modules
7. Data structures
8. File handling
9. OOP

This repository is structured to match that learning path. The code is split into separate modules instead of one long script, which matches the assignment guidance for modular, reusable code.

## What The Rubric Actually Requires

The assignment criteria explicitly require:

- Clean package structure
- Separate modules for each topic
- Meaningful naming conventions
- PEP 8 formatting
- Type hints
- Docstrings for classes and functions
- Original work that you can explain
- At least 10 logical commits for the final submission

These are the core expectations.

## Recommended Quality Improvements

These are useful, but they are quality upgrades rather than separate assignment questions:

- Return values from functions instead of only printing, so the code is easier to test
- Handle empty-input edge cases where functions use `max()` or similar operations
- Add small unit tests for each topic
- Preserve the original order when removing duplicates if that matters for the exercise
- Add brief comments only where the logic is not obvious

## How To Run

Use the file you want to practice directly with Python:

```bash
python PalakMeena_python_training/python_basic/basics/variables_and_types.py
python PalakMeena_python_training/python_basic/collections/data_structures.py
python PalakMeena_python_training/python_advance/oop/models.py
```

If you prefer package-style execution from the repository root, keep the `__init__.py` files in place so imports work correctly.

## Notes

- `requirements.txt` is intentionally minimal because the exercises mostly use Python built-ins.
- The root `README.md` exists to give a single place to understand the training repository.
- If additional topic files are added later, update this document so the structure stays accurate.