package javacore.practice.practice_2;

import java.util.Arrays;

public class DSHH {
    private Goods[] list;
    private int count;

    public DSHH(int capacity) {
        this.list = new Goods[capacity <= 0 ? 10 : capacity];
        this.count = 0;
    }

    public DSHH() {
        this(10);
    }

    public Goods find(String productCode) {
        if (productCode == null) return null;
        for (int i = 0; i < count; i++) {
            if (list[i].getProductCode().equalsIgnoreCase(productCode.trim())) {
                return list[i];
            }
        }
        return null;
    }

    public boolean addGoods(Goods item) {
        if (item == null || find(item.getProductCode()) != null) {
            return false;
        }
        if (count == list.length) {
            list = Arrays.copyOf(list, list.length * 2);
        }
        list[count++] = item;
        return true;
    }

    public boolean removeGoods(String productCode) {
        if (productCode == null) return false;
        for (int i = 0; i < count; i++) {
            if (list[i].getProductCode().equalsIgnoreCase(productCode.trim())) {
                for (int j = i; j < count - 1; j++) {
                    list[j] = list[j + 1];
                }
                list[--count] = null;
                return true;
            }
        }
        return false;
    }

    public int getCount() {
        return count;
    }

    public Goods[] getList() {
        return Arrays.copyOf(list, count);
    }

    public int getTotalFoodStock() {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            if (list[i] instanceof Food) sum += list[i].getInventoryQuantity();
        }
        return sum;
    }

    public int getTotalElectronicStock() {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            if (list[i] instanceof Electronic) sum += list[i].getInventoryQuantity();
        }
        return sum;
    }

    public int getTotalCrockeryStock() {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            if (list[i] instanceof Crockery) sum += list[i].getInventoryQuantity();
        }
        return sum;
    }

    public double getTotalFoodVAT() {
        double sum = 0;
        for (int i = 0; i < count; i++) {
            if (list[i] instanceof Food) sum += list[i].getVAT() * list[i].getInventoryQuantity();
        }
        return sum;
    }

    public double getTotalElectronicVAT() {
        double sum = 0;
        for (int i = 0; i < count; i++) {
            if (list[i] instanceof Electronic) sum += list[i].getVAT() * list[i].getInventoryQuantity();
        }
        return sum;
    }

    public double getTotalCrockeryVAT() {
        double sum = 0;
        for (int i = 0; i < count; i++) {
            if (list[i] instanceof Crockery) sum += list[i].getVAT() * list[i].getInventoryQuantity();
        }
        return sum;
    }

    public void displayAll() {
        if (count == 0) {
            System.out.println("Danh sách hàng hóa đang rỗng!");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + list[i]);
        }
    }

    public void displayManagerReport() {
        System.out.println("==========================================================================");
        System.out.println("             BÁO CÁO THỐNG KÊ TỒN KHO & TIỀN THUẾ VAT CHO QUẢN LÝ        ");
        System.out.println("==========================================================================");
        System.out.printf("%-20s | %-16s | %-20s%n", "Loại Hàng Hóa", "Tổng Số Lượng Tồn", "Tổng Tiền VAT Tồn Kho");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-20s | %-16d | %,20.0f VNĐ%n", "Hàng Thực Phẩm", getTotalFoodStock(), getTotalFoodVAT());
        System.out.printf("%-20s | %-16d | %,20.0f VNĐ%n", "Hàng Điện Máy", getTotalElectronicStock(), getTotalElectronicVAT());
        System.out.printf("%-20s | %-16d | %,20.0f VNĐ%n", "Hàng Sành Sứ", getTotalCrockeryStock(), getTotalCrockeryVAT());
        System.out.println("--------------------------------------------------------------------------");
        int totalStock = getTotalFoodStock() + getTotalElectronicStock() + getTotalCrockeryStock();
        double totalVAT = getTotalFoodVAT() + getTotalElectronicVAT() + getTotalCrockeryVAT();
        System.out.printf("%-20s | %-16d | %,20.0f VNĐ%n", "TỔNG CỘNG", totalStock, totalVAT);
        System.out.println("==========================================================================");
    }
}
