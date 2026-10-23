package javacore.practice.practice_2;

public class Main {
    public static void main(String[] args) {
        DSHH inventory = new DSHH();
        initSampleData(inventory);
        ConsoleUI.start(inventory);
    }

    private static void initSampleData(DSHH inventory) {
        inventory.addGoods(new Food("TP01", "Gạo Nàng Hương", 20, 25000,
                java.time.LocalDate.now().minusMonths(2), java.time.LocalDate.now().plusMonths(6), "Công ty Lương Thực"));
        inventory.addGoods(new Food("TP02", "Sữa tiệt trùng", 15, 12000,
                java.time.LocalDate.now().minusDays(20), java.time.LocalDate.now().minusDays(2), "Vinamilk"));
        inventory.addGoods(new Electronic("DM01", "Tủ lạnh Panasonic", 2, 8500000, 24, 1.2));
        inventory.addGoods(new Electronic("DM02", "Tivi Sony 55 inch", 8, 12000000, 12, 0.25));
        inventory.addGoods(new Crockery("SS01", "Bộ ấm chén Bát Tràng", 75, 450000,
                "Gốm Bát Tràng", java.time.LocalDate.now().minusDays(20)));
        inventory.addGoods(new Crockery("SS02", "Dĩa sứ Minh Long", 30, 80000,
                "Gốm Minh Long", java.time.LocalDate.now().minusDays(5)));
    }
}
