import java.util.Scanner;

/**
 * Program to print the Fibonacci sequence up to a specified number of terms.
 */
public class FibonacciSequence {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of terms to print: ");
        int terms = scanner.nextInt();

        if (terms <= 0) {
            System.out.println("Please enter a positive number.");
        } else {
            System.out.print("Fibonacci Sequence: ");
            long first = 0, second = 1;

            for (int i = 1; i <= terms; i++) {
                System.out.print(first);
                if (i < terms) {
                    System.out.print(", ");
                }
                long next = first + second;
                first = second;
                second = next;
            }
            System.out.println();
        }

        scanner.close();
    }
}