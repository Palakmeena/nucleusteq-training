package PalakMeena_java_training.session1.strings;

import java.util.Scanner;

/**
 * Program to count the number of vowels in a given string.
 */
public class VowelCounter {

    private static final String VOWELS = "aeiouAEIOU";

    public static int countVowels(String input) {
        int count = 0;

        for (int i = 0; i < input.length(); i++) {
            if (VOWELS.indexOf(input.charAt(i)) != -1) {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a string: ");
        String input = scanner.nextLine();

        int vowelCount = countVowels(input);

        System.out.println("String        : " + input);
        System.out.println("Vowel count   : " + vowelCount);

        scanner.close();
    }
}