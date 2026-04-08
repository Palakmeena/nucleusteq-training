import java.util.Scanner;


public class AreaCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Area Calculator ===");
        System.out.println("Choose a shape:");
        System.out.println("1. Circle");
        System.out.println("2. Rectangle");
        System.out.println("3. Triangle");
        System.out.print("Enter your choice (1-3): ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.print("Enter radius of the circle: ");
                double radius = scanner.nextDouble();
                double circleArea = Math.PI * radius * radius;
                System.out.printf("Area of Circle = %.2f%n", circleArea);
                break;

            case 2:
                System.out.print("Enter length of the rectangle: ");
                double length = scanner.nextDouble();
                System.out.print("Enter breadth of the rectangle: ");
                double breadth = scanner.nextDouble();
                double rectangleArea = length * breadth;
                System.out.printf("Area of Rectangle = %.2f%n", rectangleArea);
                break;

            case 3:
                System.out.print("Enter base of the triangle: ");
                double base = scanner.nextDouble();
                System.out.print("Enter height of the triangle: ");
                double height = scanner.nextDouble();
                double triangleArea = 0.5 * base * height;
                System.out.printf("Area of Triangle = %.2f%n", triangleArea);
                break;

            default:
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        }

        scanner.close();
    }
}