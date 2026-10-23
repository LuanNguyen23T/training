package javacore.practice.practice_2;

public abstract class Goods {
    protected final String productCode;
    protected String name;
    protected int inventoryQuantity;
    protected double unitPrice;

    public Goods(String productCode, String name, int inventoryQuantity, double unitPrice) {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hàng không được để trống!");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hàng không được để trống!");
        }
        if (inventoryQuantity < 0) {
            throw new IllegalArgumentException("Số lượng tồn phải >= 0!");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Đơn giá phải >= 0!");
        }
        this.productCode = productCode.trim();
        this.name = name.trim();
        this.inventoryQuantity = inventoryQuantity;
        this.unitPrice = unitPrice;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        if (inventoryQuantity < 0) {
            throw new IllegalArgumentException("Số lượng tồn phải >= 0!");
        }
        this.inventoryQuantity = inventoryQuantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Đơn giá phải >= 0!");
        }
        this.unitPrice = unitPrice;
    }

    public abstract VatRate getVATRate();
    public double getVAT() {
        return unitPrice * getVATRate().getRate();
    }
    public abstract String measureConsumption();

    @Override
    public String toString() {
        return String.format("Mã: %-6s | Tên: %-18s | Tồn: %-4d | Đơn giá: %,12.0f | VAT: %,10.0f | Đánh giá: %s",
                productCode, name, inventoryQuantity, unitPrice, getVAT(), measureConsumption());
    }
}
