package javacore.practice.practice_3;

import java.time.Year;
import java.util.Set;

public abstract class Vehicle {
    protected final String vehicleNumber;
    protected String manufacturer;
    protected int yearOfManufacture;
    protected String color;
    protected Owner owner;

    private static final Set<String> VALID_MANUFACTURERS = Set.of("Honda", "Yamaha", "Toyota", "Suzuki");

    public Vehicle(String vehicleNumber, String manufacturer, int yearOfManufacture, String color, Owner owner) {
        if (vehicleNumber == null || vehicleNumber.trim().length() != 5) {
            throw new IllegalArgumentException("Biển số xe phải có đúng 5 ký tự!");
        }
        this.vehicleNumber = vehicleNumber.trim().toUpperCase();
        setManufacturer(manufacturer);
        setYearOfManufacture(yearOfManufacture);
        setColor(color);
        setOwner(owner);
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        if (manufacturer == null) {
            throw new IllegalArgumentException("Hãng sản xuất không được null!");
        }
        for (String valid : VALID_MANUFACTURERS) {
            if (valid.equalsIgnoreCase(manufacturer.trim())) {
                this.manufacturer = valid;
                return;
            }
        }
        throw new IllegalArgumentException("Hãng sản xuất chỉ được là: Honda, Yamaha, Toyota, Suzuki!");
    }

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        int currentYear = Year.now().getValue();
        if (yearOfManufacture <= 2000 || yearOfManufacture > currentYear) {
            throw new IllegalArgumentException("Năm sản xuất phải lớn hơn 2000 và nhỏ hơn hoặc bằng " + currentYear + "!");
        }
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("Màu xe không được để trống!");
        }
        this.color = color.trim();
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Chủ xe không được để trống!");
        }
        this.owner = owner;
    }

    public abstract String getVehicleType();

    @Override
    public String toString() {
        return String.format("Loại: %-10s | Biển số: %-6s | Hãng: %-7s | Năm: %-4d | Màu: %-8s | %s",
                getVehicleType(), vehicleNumber, manufacturer, yearOfManufacture, color, owner);
    }
}
