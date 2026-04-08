package PalakMeena_java_training.session1.control_flow;

import java.util.Scanner;

/**
 * Program to find the largest number among three given numbers.
 */
public class LargestOfThree {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first number:  ");
        double num1 = scanner.nextDouble();

        System.out.print("Enter second number: ");
        double num2 = scanner.nextDouble();

        System.out.print("Enter third number:  ");
        double num3 = scanner.nextDouble();

        double largest;

        if (num1 >= num2 && num1 >= num3) {
            largest = num1;
        } else if (num2 >= num1 && num2 >= num3) {
            largest = num2;
        } else {
            largest = num3;
        }

        System.out.println("The largest number is: " + largest);

        scanner.close();
    }
}