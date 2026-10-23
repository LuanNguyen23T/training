package javacore.practice.practice_2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Food extends Goods {
    private LocalDate manufactureDate;
    private LocalDate expirationDate;
    private String supplier;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Food(String productCode, String name, int inventoryQuantity, double unitPrice,
                LocalDate manufactureDate, LocalDate expirationDate, String supplier) {
        super(productCode, name, inventoryQuantity, unitPrice);
        if (expirationDate.isBefore(manufactureDate)) {
            throw new IllegalArgumentException("Ngày hết hạn phải sau hoặc bằng ngày sản xuất!");
        }
        this.manufactureDate = manufactureDate;
        this.expirationDate = expirationDate;
        this.supplier = supplier;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        if (this.expirationDate.isBefore(manufactureDate)) {
            throw new IllegalArgumentException("Ngày hết hạn phải sau hoặc bằng ngày sản xuất!");
        }
        this.manufactureDate = manufactureDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        if (expirationDate.isBefore(this.manufactureDate)) {
            throw new IllegalArgumentException("Ngày hết hạn phải sau hoặc bằng ngày sản xuất!");
        }
        this.expirationDate = expirationDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    @Override
    public VatRate getVATRate() {
        return VatRate.FOOD;
    }

    @Override
    public String measureConsumption() {
        if (inventoryQuantity > 0 && expirationDate.isBefore(LocalDate.now())) {
            return "Khó bán";
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | NSX: %s | HSD: %s | NCC: %s",
                manufactureDate.format(FORMATTER), expirationDate.format(FORMATTER), supplier);
    }
}
