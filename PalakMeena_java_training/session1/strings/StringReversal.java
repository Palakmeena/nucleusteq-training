package PalakMeena_java_training.session1.strings;

import java.util.Scanner;

/**
 * Program to reverse a given string.
 */
public class StringReversal {

    public static String reverse(String input) {
        char[] characters = input.toCharArray();
        int left = 0;
        int right = characters.length - 1;

        while (left < right) {
            char temp = characters[left];
            characters[left] = characters[right];
            characters[right] = temp;
            left++;
            right--;
        }

        return new String(characters);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a string to reverse: ");
        String input = scanner.nextLine();
        String reversed = reverse(input);

        System.out.println("Original : " + input);
        System.out.println("Reversed : " + reversed);

        scanner.close();
    }
}