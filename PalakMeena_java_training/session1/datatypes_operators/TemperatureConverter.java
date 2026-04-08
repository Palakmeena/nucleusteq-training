package PalakMeena_java_training.session1.datatypes_operators;

import java.util.Scanner;

/**
 * Program to convert temperature between Celsius and Fahrenheit.
 */
public class TemperatureConverter {

    
    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9.0 / 5.0) + 32;
    }

    
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5.0 / 9.0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Temperature Converter ===");
        System.out.println("1. Celsius to Fahrenheit");
        System.out.println("2. Fahrenheit to Celsius");
        System.out.print("Enter choice (1 or 2): ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.print("Enter temperature in Celsius: ");
                double celsius = scanner.nextDouble();
                System.out.printf("%.2f°C = %.2f°F%n", celsius, celsiusToFahrenheit(celsius));
                break;

            case 2:
                System.out.print("Enter temperature in Fahrenheit: ");
                double fahrenheit = scanner.nextDouble();
                System.out.printf("%.2f°F = %.2f°C%n", fahrenheit, fahrenheitToCelsius(fahrenheit));
                break;

            default:
                System.out.println("Invalid choice.");
        }

        scanner.close();
    }
}