package javacore.practice.practice_1;

public class Main {
    public static void main(String[] args) {
        try {
            Shape<Double> genericShape = new Shape<>(10.5, 5.2);
            genericShape.displayInfo();
            System.out.println();

            Rectangle<Integer> rectangle = new Rectangle<>(8, 4);
            rectangle.displayInfo();
            System.out.println();

            Circle<Double> circle = new Circle<>(5.0);
            circle.displayInfo();
            System.out.println();

            Rectangle<Integer> rectangleError = new Rectangle<>(-3, 4);
            rectangleError.displayInfo();
            System.out.println();

        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
