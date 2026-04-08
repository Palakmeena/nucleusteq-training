package PalakMeena_java_training.session1.arrays;

import java.util.Scanner;

/**
 * Program to search for a specific element in an array using Linear Search.
 */
public class LinearSearch {

    public static int linearSearch(int[] array, int target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

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

        System.out.print("Enter the element to search for: ");
        int target = scanner.nextInt();

        int index = linearSearch(numbers, target);

        if (index != -1) {
            System.out.println("Element " + target + " found at index " + index + " (position " + (index + 1) + ").");
        } else {
            System.out.println("Element " + target + " not found in the array.");
        }

        scanner.close();
    }
}