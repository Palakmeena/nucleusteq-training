package PalakMeena_java_training.session1.advanced_topics;

//Example of Exception Handling in Java

public class ExceptionHandler {

    public static void main(String[] args) {
        int[] numbers = { 10, 20, 30 };

        try {
            // 1. Potential ArithmeticException (Division by zero)
            int result = numbers[0] / 0;
            System.out.println("Result: " + result);

            // 2. Potential ArrayIndexOutOfBoundsException
            System.out.println("Accessing index 5: " + numbers[5]);

        } catch (ArithmeticException e) {
            System.err.println("Error: Logic failure. " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: You tried to access a position that doesn't exist in the array.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            System.out.println("Clean-up: Execution of the try-catch block is complete.");
        }

        System.out.println("Program continues to run smoothly after handling the error!");
    }
}
