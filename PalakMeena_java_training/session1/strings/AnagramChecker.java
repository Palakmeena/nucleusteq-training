package PalakMeena_java_training.session1.strings;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Program to check if two strings are anagrams.
 */
public class AnagramChecker {

    public static boolean areAnagrams(String first, String second) {
        String cleanFirst = first.replaceAll("\\s", "").toLowerCase();
        String cleanSecond = second.replaceAll("\\s", "").toLowerCase();

        if (cleanFirst.length() != cleanSecond.length()) {
            return false;
        }

        char[] firstArray = cleanFirst.toCharArray();
        char[] secondArray = cleanSecond.toCharArray();

        Arrays.sort(firstArray);
        Arrays.sort(secondArray);

        return Arrays.equals(firstArray, secondArray);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first string  : ");
        String first = scanner.nextLine();

        System.out.print("Enter second string : ");
        String second = scanner.nextLine();

        if (areAnagrams(first, second)) {
            System.out.println("\"" + first + "\" and \"" + second + "\" ARE anagrams.");
        } else {
            System.out.println("\"" + first + "\" and \"" + second + "\" are NOT anagrams.");
        }

        scanner.close();
    }
}