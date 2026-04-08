package PalakMeena_java_training.session1.oop;

public class GraduateStudent extends Student {
    private String researchTopic;

    public GraduateStudent(String name, int rollNumber, double marks, String researchTopic) {
        super(name, rollNumber, marks);
        this.researchTopic = researchTopic;
    }

    // Method Overriding (Polymorphism)
    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Research Topic: " + researchTopic);
    }

    public static void main(String[] args) {
        Student s1 = new Student("Palak", 101, 85.0);
        GraduateStudent g1 = new GraduateStudent("Alex", 202, 92.0, "Artificial Intelligence");

        System.out.println("--- Student Details ---");
        s1.displayDetails();
        
        System.out.println("\n--- Graduate Student Details ---");
        g1.displayDetails(); // Demonstrates overriding
    }
}