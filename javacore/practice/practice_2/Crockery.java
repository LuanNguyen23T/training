package javacore.practice.practice_2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Crockery extends Goods {
    private String manufacturer;
    private LocalDate arrivalDate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Crockery(String productCode, String name, int inventoryQuantity, double unitPrice,
                    String manufacturer, LocalDate arrivalDate) {
        super(productCode, name, inventoryQuantity, unitPrice);
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            throw new IllegalArgumentException("Nhà sản xuất không được để trống!");
        }
        if (arrivalDate == null) {
            throw new IllegalArgumentException("Ngày nhập kho không được null!");
        }
        this.manufacturer = manufacturer.trim();
        this.arrivalDate = arrivalDate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public long getDaysInStorage() {
        return ChronoUnit.DAYS.between(arrivalDate, LocalDate.now());
    }

    @Override
    public VatRate getVATRate() {
        return VatRate.CROCKERY;
    }

    @Override
    public String measureConsumption() {
        if (inventoryQuantity > 50 && getDaysInStorage() > 10) {
            return String.format("Bán chậm (Tồn: %d, Lưu kho: %d ngày)", inventoryQuantity, getDaysInStorage());
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | NSX: %s | Ngày nhập: %s (Lưu kho: %d ngày)",
                manufacturer, arrivalDate.format(FORMATTER), getDaysInStorage());
    }
}
