"""
Q36. Explain the advantages of using an IDE debugger
over print statements.
"""


def display_debugger_advantages() -> None:
    """
    Displays debugger advantages.
    """

    advantages = [
        "Inspect variables in real time.",
        "Execute code step by step.",
        "Set breakpoints at specific lines.",
        "Track call stack information.",
        "Debug complex applications more efficiently.",
        "Avoid excessive print statements.",
        "Quickly identify logical errors."
    ]

    print("Advantages of IDE Debuggers:\n")

    for advantage in advantages:
        print(f"- {advantage}")


if __name__ == "__main__":
    display_debugger_advantages()