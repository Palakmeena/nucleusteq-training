package PalakMeena_java_training.session1.advanced_topics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//Example of File I/O in Java to read content from a file and display it on the console

public class FileReadExample {
    public static void main(String[] args) {
      
        String fileName = "data.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            System.out.println("--- Reading File Content ---");
            
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}