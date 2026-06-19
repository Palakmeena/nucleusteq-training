"""
Q44. Create multiple threads to simulate
file downloading using time.sleep().
"""

import threading
import time


def download_file(file_name: str) -> None:
    """
    Simulates file downloading.
    """
    print(f"Downloading {file_name}...")

    time.sleep(2)

    print(f"{file_name} Download Complete")


if __name__ == "__main__":
    file_one = threading.Thread(
        target=download_file,
        args=("file1.pdf",)
    )

    file_two = threading.Thread(
        target=download_file,
        args=("file2.pdf",)
    )

    file_three = threading.Thread(
        target=download_file,
        args=("file3.pdf",)
    )

    file_one.start()
    file_two.start()
    file_three.start()

    file_one.join()
    file_two.join()
    file_three.join()

    print("All Downloads Finished")