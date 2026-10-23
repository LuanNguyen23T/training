package javacore.practice.practice_3;

public class Motorbike extends Vehicle {
    private double capacity;

    public Motorbike(String vehicleNumber, String manufacturer, int yearOfManufacture, String color, Owner owner,
                     double capacity) {
        super(vehicleNumber, manufacturer, yearOfManufacture, color, owner);
        setCapacity(capacity);
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Công suất/dung tích phải > 0!");
        }
        this.capacity = capacity;
    }

    @Override
    public String getVehicleType() {
        return "Motorbike";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Dung tích/Công suất: %.1f cc", capacity);
    }
}
