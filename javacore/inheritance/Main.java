package javacore.inheritance;

public class Main {
    public static void main(String[] args) {
        Geometry[] shapes = new Geometry[] {
            new Circle(),
            new Square(),
            new Pediment(),
            new Rectangle()
        };

        for (Geometry shape : shapes) {
            shape.show();
        }
    }
}

class Geometry {
    public void show() {
        System.out.println("Geometry");
    }
}

class Circle extends Geometry {
    @Override
    public void show() {
        System.out.println("Circle");
    }
}

class Square extends Geometry {
    @Override
    public void show() {
        System.out.println("Square");
    }
}

class Pediment extends Geometry {
    @Override
    public void show() {
        System.out.println("Pediment");
    }
}

class Rectangle extends Geometry {
    @Override
    public void show() {
        System.out.println("Rectangle");
    }
}
