package PalakMeena_java_training.session1.arrays;

import java.util.Scanner;
 
/**
 * Program to find the average of elements in an array.
 */
public class ArrayAverage {
 
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
 
        System.out.print("Enter the number of elements: ");
        int size = scanner.nextInt();
 
        int[] numbers = new int[size];
 
        System.out.println("Enter the elements:");
        for (int i = 0; i < size; i++) {
            System.out.print("Element " + (i + 1) + ": ");
            numbers[i] = scanner.nextInt();
        }
 
        double sum = 0;
        for (int num : numbers) {
            sum += num;
        }
 
        double average = sum / size;
        System.out.printf("Average of the array elements = %.2f%n", average);
 
        scanner.close();
    }
}