
package PalakMeena_java_training.session1.advanced_topics;

//Example of abstract classes to create a shape hierarchy 

// Level 1: The Abstract Base
abstract class Shape {
    protected String name;

    public Shape(String name) {
        this.name = name;
    }

    // Concrete method: All shapes share this
    public void printDescription() {
        System.out.println("Object: " + name);
    }

    // Abstract method: Must be implemented by children
    abstract double getArea();
}

// Level 2: Subclasses
class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        super("Circle");
        this.radius = radius;
    }

    @Override
    double getArea() {
        return Math.PI * radius * radius;
    }
}

class Triangle extends Shape {
    private double base, height;

    public Triangle(double base, double height) {
        super("Triangle");
        this.base = base;
        this.height = height;
    }

    @Override
    double getArea() {
        return 0.5 * base * height;
    }
}

// Level 2: Rectangle (Parent to Square)
class Rectangle extends Shape {
    protected double length, width;

    public Rectangle(double length, double width) {
        super("Rectangle");
        this.length = length;
        this.width = width;
    }

    // To handle the name for Square
    protected Rectangle(String name, double length, double width) {
        super(name);
        this.length = length;
        this.width = width;
    }

    @Override
    double getArea() {
        return length * width;
    }
}

// Level 3: Square (Specialized Rectangle)
class Square extends Rectangle {
    public Square(double side) {
        super("Square", side, side); // Reusing Rectangle logic
    }
}

// Main Class
public class ShapeHierarchy {
    public static void main(String[] args) {
        // Demonstrate Polymorphism with an array
        Shape[] myShapes = {
            new Circle(7.0),
            new Rectangle(10.0, 5.0),
            new Triangle(8.0, 4.0),
            new Square(6.0)
        };

        System.out.println("--- Area Calculations ---");
        for (Shape s : myShapes) {
            s.printDescription();
            System.out.printf("Area: %.2f\n", s.getArea());
            System.out.println("-------------------------");
        }
    }
}