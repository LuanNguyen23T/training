package javacore.practice.practice_1;

public class Rectangle<T extends Number> extends Shape<T> {

    public Rectangle(T width, T height) {
        super(width, height);
    }

    public double getArea() {
        return width.doubleValue() * height.doubleValue();
    }

    public double getPerimeter() {
        return 2.0 * (width.doubleValue() + height.doubleValue());
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.printf("Area     : %.2f%n", getArea());
        System.out.printf("Perimeter: %.2f%n", getPerimeter());
    }
}
