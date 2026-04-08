import java.util.Scanner;

/**
 * Program to find the factorial of a given number.
 * Handles both iterative and recursive approaches.
 */
public class FactorialCalculator {

    public static long factorialIterative(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static long factorialRecursive(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorialRecursive(n - 1);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a non-negative integer: ");
        int number = scanner.nextInt();

        if (number < 0) {
            System.out.println("Factorial is not defined for negative numbers.");
        } else {
            System.out.println("Factorial of " + number + " (iterative) = " + factorialIterative(number));
            System.out.println("Factorial of " + number + " (recursive) = " + factorialRecursive(number));
        }

        scanner.close();
    }
}