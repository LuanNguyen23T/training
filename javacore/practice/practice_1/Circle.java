package javacore.practice.practice_1;

public class Circle<T extends Number> extends Shape<T> {

    public Circle(T radius) {
        super(radius, radius);
    }

    public double getArea() {
        double r = width.doubleValue();
        return Math.PI * r * r;
    }

    public double getCircumference() {
        double d = width.doubleValue() * 2.0;
        return d * 3.14;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.printf("Area         : %.2f%n", getArea());
        System.out.printf("Circumference: %.2f%n", getCircumference());
    }
}
