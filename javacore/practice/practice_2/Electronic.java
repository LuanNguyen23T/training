package javacore.practice.practice_2;

public class Electronic extends Goods {
    private int warrantyMonths;
    private double capacityKW;

    public Electronic(String productCode, String name, int inventoryQuantity, double unitPrice,
                      int warrantyMonths, double capacityKW) {
        super(productCode, name, inventoryQuantity, unitPrice);
        if (warrantyMonths < 0) {
            throw new IllegalArgumentException("Thời gian bảo hành phải >= 0 tháng!");
        }
        if (capacityKW < 0) {
            throw new IllegalArgumentException("Công suất phải >= 0 KW!");
        }
        this.warrantyMonths = warrantyMonths;
        this.capacityKW = capacityKW;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        if (warrantyMonths < 0) {
            throw new IllegalArgumentException("Thời gian bảo hành phải >= 0 tháng!");
        }
        this.warrantyMonths = warrantyMonths;
    }

    public double getCapacityKW() {
        return capacityKW;
    }

    public void setCapacityKW(double capacityKW) {
        if (capacityKW < 0) {
            throw new IllegalArgumentException("Công suất phải >= 0 KW!");
        }
        this.capacityKW = capacityKW;
    }

    @Override
    public VatRate getVATRate() {
        return VatRate.ELECTRONIC;
    }

    @Override
    public String measureConsumption() {
        if (inventoryQuantity < 3) {
            return "Bán được";
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Bảo hành: %d tháng | Công suất: %.2f KW",
                warrantyMonths, capacityKW);
    }
}
