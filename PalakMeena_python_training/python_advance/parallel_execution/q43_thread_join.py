"""
Q43. Demonstrate the use of join().
"""

import threading
import time


def task() -> None:
    """
    Simulates a task.
    """
    print("Task Started")
    time.sleep(2)
    print("Task Finished")


if __name__ == "__main__":
    worker_thread = threading.Thread(target=task)

    worker_thread.start()

    worker_thread.join()

    print("Main Thread Finished")