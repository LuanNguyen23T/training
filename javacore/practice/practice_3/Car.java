package javacore.practice.practice_3;

public class Car extends Vehicle {
    private int numberOfSeats;
    private String engineType;

    public Car(String vehicleNumber, String manufacturer, int yearOfManufacture, String color, Owner owner,
               int numberOfSeats, String engineType) {
        super(vehicleNumber, manufacturer, yearOfManufacture, color, owner);
        setNumberOfSeats(numberOfSeats);
        setEngineType(engineType);
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        if (numberOfSeats <= 0) {
            throw new IllegalArgumentException("Số chỗ ngồi phải > 0!");
        }
        this.numberOfSeats = numberOfSeats;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        if (engineType == null || engineType.trim().isEmpty()) {
            throw new IllegalArgumentException("Kiểu động cơ không được để trống!");
        }
        this.engineType = engineType.trim();
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Số chỗ: %d | Động cơ: %s", numberOfSeats, engineType);
    }
}
