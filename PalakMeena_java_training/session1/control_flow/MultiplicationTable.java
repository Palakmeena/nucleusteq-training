package PalakMeena_java_training.session1.control_flow;

import java.util.Scanner;

/**
 * Program to print a multiplication table using a for loop.
 */
public class MultiplicationTable {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a number to print its multiplication table: ");
        int number = scanner.nextInt();

        System.out.println("\nMultiplication Table of " + number + ":");
        System.out.println("---------------------------");

        for (int i = 1; i <= 10; i++) {
            System.out.printf("%d x %2d = %d%n", number, i, number * i);
        }

        scanner.close();
    }
}