import java.util.Scanner;

/**
 * Program to print patterns like a right triangle and a square using loops.
 */
public class PatternPrinter {

    public static void printTriangle(int rows) {
        System.out.println("Right-Angled Triangle Pattern:");
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }

    public static void printSquare(int size) {
        System.out.println("Square Pattern:");
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of rows for the triangle: ");
        int triangleRows = scanner.nextInt();
        printTriangle(triangleRows);

        System.out.println();

        System.out.print("Enter size for the square: ");
        int squareSize = scanner.nextInt();
        printSquare(squareSize);

        scanner.close();
    }
}