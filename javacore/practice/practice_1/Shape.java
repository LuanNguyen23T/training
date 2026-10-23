package javacore.practice.practice_1;

public class Shape<T extends Number> {
    protected T width;
    protected T height;

    public Shape(T width, T height) {
        validateDimension(width, "Width");
        validateDimension(height, "Height");
        this.width = width;
        this.height = height;
    }

    protected void validateDimension(T value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new InvalidShapeException(fieldName + " phải lớn hơn 0 (nhận giá trị: " + value + ")");
        }
    }

    public T getWidth() {
        return width;
    }

    public void setWidth(T width) {
        validateDimension(width, "Width");
        this.width = width;
    }

    public T getHeight() {
        return height;
    }

    public void setHeight(T height) {
        validateDimension(height, "Height");
        this.height = height;
    }

    public void displayInfo() {
        System.out.printf("Width : %s%n", width);
        System.out.printf("Height: %s%n", height);
    }
}
