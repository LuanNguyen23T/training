package javacore.practice.practice_2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ConsoleUI {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void start(DSHH inventory) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        do {
            printMenu();
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                choice = Integer.parseInt(line);
                handleChoice(choice, scanner, inventory);
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập số nguyên!");
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } while (choice != 0);
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\\n=======================================================");
        System.out.println("       HỆ THỐNG QUẢN LÝ HÀNG HÓA SIÊU THỊ             ");
        System.out.println("=======================================================");
        System.out.println("1. Xuất danh sách toàn bộ hàng hóa");
        System.out.println("2. Xem báo cáo tồn kho & tiền thuế VAT cho Quản lý");
        System.out.println("3. Thêm Hàng Thực Phẩm (Food)");
        System.out.println("4. Thêm Hàng Điện Máy (Electronic)");
        System.out.println("5. Thêm Hàng Sành Sứ (Crockery)");
        System.out.println("6. Xóa hàng hóa theo mã (Bớt hàng)");
        System.out.println("7. Tìm kiếm hàng hóa theo mã");
        System.out.println("0. Thoát chương trình");
        System.out.print("Vui lòng chọn chức năng (0-7): ");
    }

    private static void handleChoice(int choice, Scanner scanner, DSHH inventory) {
        switch (choice) {
            case 1:
                System.out.println("\\n--- DANH SÁCH HÀNG HÓA TRONG KHO ---");
                inventory.displayAll();
                break;
            case 2:
                inventory.displayManagerReport();
                break;
            case 3:
                addFood(scanner, inventory);
                break;
            case 4:
                addElectronic(scanner, inventory);
                break;
            case 5:
                addCrockery(scanner, inventory);
                break;
            case 6:
                removeGoods(scanner, inventory);
                break;
            case 7:
                findGoods(scanner, inventory);
                break;
            case 0:
                System.out.println("Đã thoát chương trình. Tạm biệt!");
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại!");
        }
    }

    private static void addFood(Scanner scanner, DSHH inventory) {
        try {
            System.out.println("\\n--- THÊM MỚI HÀNG THỰC PHẨM ---");
            System.out.print("Nhập mã hàng: ");
            String code = scanner.nextLine();
            System.out.print("Nhập tên hàng: ");
            String name = scanner.nextLine();
            System.out.print("Nhập số lượng tồn: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            System.out.print("Nhập đơn giá: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Nhập ngày sản xuất (dd/MM/yyyy): ");
            LocalDate mfg = LocalDate.parse(scanner.nextLine().trim(), FORMATTER);
            System.out.print("Nhập ngày hết hạn (dd/MM/yyyy): ");
            LocalDate exp = LocalDate.parse(scanner.nextLine().trim(), FORMATTER);
            System.out.print("Nhập nhà cung cấp: ");
            String supplier = scanner.nextLine();
            Food food = new Food(code, name, quantity, price, mfg, exp, supplier);
            System.out.println(inventory.addGoods(food) ? "=> Thêm hàng thực phẩm thành công!" : "=> Thêm thất bại: Mã hàng đã tồn tại!");
        } catch (Exception e) {
            System.out.println("=> Lỗi khi nhập: " + e.getMessage());
        }
    }

    private static void addElectronic(Scanner scanner, DSHH inventory) {
        try {
            System.out.println("\\n--- THÊM MỚI HÀNG ĐIỆN MÁY ---");
            System.out.print("Nhập mã hàng: ");
            String code = scanner.nextLine();
            System.out.print("Nhập tên hàng: ");
            String name = scanner.nextLine();
            System.out.print("Nhập số lượng tồn: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            System.out.print("Nhập đơn giá: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Nhập thời gian bảo hành (tháng): ");
            int warranty = Integer.parseInt(scanner.nextLine());
            System.out.print("Nhập công suất (KW): ");
            double capacity = Double.parseDouble(scanner.nextLine());
            Electronic electronic = new Electronic(code, name, quantity, price, warranty, capacity);
            System.out.println(inventory.addGoods(electronic) ? "=> Thêm hàng điện máy thành công!" : "=> Thêm thất bại: Mã hàng đã tồn tại!");
        } catch (Exception e) {
            System.out.println("=> Lỗi khi nhập: " + e.getMessage());
        }
    }

    private static void addCrockery(Scanner scanner, DSHH inventory) {
        try {
            System.out.println("\\n--- THÊM MỚI HÀNG SÀNH SỨ ---");
            System.out.print("Nhập mã hàng: ");
            String code = scanner.nextLine();
            System.out.print("Nhập tên hàng: ");
            String name = scanner.nextLine();
            System.out.print("Nhập số lượng tồn: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            System.out.print("Nhập đơn giá: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Nhập nhà sản xuất: ");
            String manufacturer = scanner.nextLine();
            System.out.print("Nhập ngày nhập kho (dd/MM/yyyy): ");
            LocalDate arrival = LocalDate.parse(scanner.nextLine().trim(), FORMATTER);
            Crockery crockery = new Crockery(code, name, quantity, price, manufacturer, arrival);
            System.out.println(inventory.addGoods(crockery) ? "=> Thêm hàng sành sứ thành công!" : "=> Thêm thất bại: Mã hàng đã tồn tại!");
        } catch (Exception e) {
            System.out.println("=> Lỗi khi nhập: " + e.getMessage());
        }
    }

    private static void removeGoods(Scanner scanner, DSHH inventory) {
        System.out.print("Nhập mã hàng cần xóa: ");
        String code = scanner.nextLine();
        System.out.println(inventory.removeGoods(code) ? "=> Xóa hàng hóa thành công!" : "=> Xóa thất bại: Không tìm thấy mã hàng " + code);
    }

    private static void findGoods(Scanner scanner, DSHH inventory) {
        System.out.print("Nhập mã hàng cần tìm: ");
        String code = scanner.nextLine();
        Goods item = inventory.find(code);
        System.out.println(item != null ? "=> Tìm thấy hàng hóa: " + item : "=> Không tìm thấy hàng hóa có mã: " + code);
    }
}
