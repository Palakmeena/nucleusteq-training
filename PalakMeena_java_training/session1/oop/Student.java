package PalakMeena_java_training.session1.oop;

/**
 * Represents a Student with basic academic attributes.
 **/
public class Student {
    protected String name;
    protected int rollNumber;
    protected double marks;

    public Student(String name, int rollNumber, double marks) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.marks = marks;
    }

    // Method to be overridden (Polymorphism)
    public void displayDetails() {
        System.out.println("Student: " + name + ", Roll: " + rollNumber + ", Marks: " + marks);
    }

    // Method Overloading (Polymorphism)
    public void updateMarks(double newMarks) {
        this.marks = newMarks;
        System.out.println("Marks updated to: " + marks);
    }

    public void updateMarks(double newMarks, double bonus) {
        this.marks = newMarks + bonus;
        System.out.println("Marks updated with bonus to: " + marks);
    }
}