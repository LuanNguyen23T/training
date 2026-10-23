package javacore.practice.practice_3;

public class Truck extends Vehicle {
    private double tonnage;

    public Truck(String vehicleNumber, String manufacturer, int yearOfManufacture, String color, Owner owner,
                 double tonnage) {
        super(vehicleNumber, manufacturer, yearOfManufacture, color, owner);
        setTonnage(tonnage);
    }

    public double getTonnage() {
        return tonnage;
    }

    public void setTonnage(double tonnage) {
        if (tonnage <= 0) {
            throw new IllegalArgumentException("Trọng tải phải > 0!");
        }
        this.tonnage = tonnage;
    }

    @Override
    public String getVehicleType() {
        return "Truck";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Trọng tải: %.2f tấn", tonnage);
    }
}
