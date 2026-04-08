package PalakMeena_java_training.session1.datatypes_operators;

import java.util.Scanner;
 
/**
 * Demonstrates arithmetic, logical, and relational operators in Java.
 */
public class OperatorsDemo {
 
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
 
        System.out.print("Enter first number: ");
        double a = scanner.nextDouble();
 
        System.out.print("Enter second number: ");
        double b = scanner.nextDouble();
 
        // Arithmetic Operators
        System.out.println("\n--- Arithmetic Operators ---");
        System.out.println("a + b = " + (a + b));
        System.out.println("a - b = " + (a - b));
        System.out.println("a * b = " + (a * b));
        System.out.println("a / b = " + (b != 0 ? (a / b) : "undefined (division by zero)"));
        System.out.println("a % b = " + (b != 0 ? (a % b) : "undefined (modulo by zero)"));
 
        // Relational Operators
        System.out.println("\n--- Relational Operators ---");
        System.out.println("a == b : " + (a == b));
        System.out.println("a != b : " + (a != b));
        System.out.println("a >  b : " + (a > b));
        System.out.println("a <  b : " + (a < b));
        System.out.println("a >= b : " + (a >= b));
        System.out.println("a <= b : " + (a <= b));
 
        // Logical Operators
        boolean isAPositive = a > 0;
        boolean isBPositive = b > 0;
 
        System.out.println("\n--- Logical Operators ---");
        System.out.println("a > 0 AND b > 0 : " + (isAPositive && isBPositive));
        System.out.println("a > 0 OR  b > 0 : " + (isAPositive || isBPositive));
        System.out.println("NOT (a > 0)     : " + (!isAPositive));
 
        scanner.close();
    }
}