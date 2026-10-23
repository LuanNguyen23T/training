package javacore.practice.practice_3;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {

    public static void start(VehicleManager manager) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        do {
            printMenu();
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                choice = Integer.parseInt(line);
                handleChoice(choice, scanner, manager);
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập số nguyên!");
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n=======================================================");
        System.out.println("     HỆ THỐNG QUẢN LÝ PHƯƠNG TIỆN GIAO THÔNG (POLICE) ");
        System.out.println("=======================================================");
        System.out.println("1. Thêm phương tiện giao thông");
        System.out.println("2. Tìm kiếm phương tiện theo biển số xe");
        System.out.println("3. Tìm phương tiện theo số CCCD chủ xe");
        System.out.println("4. Xóa tất cả phương tiện của một hãng sản xuất");
        System.out.println("5. Hãng sản xuất có nhiều phương tiện nhất");
        System.out.println("6. Sắp xếp phương tiện theo biển số giảm dần");
        System.out.println("7. Thống kê số lượng từng loại phương tiện");
        System.out.println("8. Xuất toàn bộ danh sách phương tiện");
        System.out.println("0. Thoát chương trình");
        System.out.print("Vui lòng chọn chức năng (0-8): ");
    }

    private static void handleChoice(int choice, Scanner scanner, VehicleManager manager) {
        switch (choice) {
            case 1:
                addVehicleMenu(scanner, manager);
                break;
            case 2:
                searchByVehicleNumber(scanner, manager);
                break;
            case 3:
                searchByOwnerCccd(scanner, manager);
                break;
            case 4:
                deleteByManufacturer(scanner, manager);
                break;
            case 5:
                System.out.println("=> Hãng có nhiều xe nhất: " + manager.getManufacturerWithMostVehicles());
                break;
            case 6:
                manager.sortByVehicleNumberDescending();
                System.out.println("=> Đã sắp xếp theo biển số giảm dần:");
                manager.displayAll();
                break;
            case 7:
                displayStatistics(manager);
                break;
            case 8:
                System.out.println("\n--- TẤT CẢ PHƯƠNG TIỆN ĐANG QUẢN LÝ ---");
                manager.displayAll();
                break;
            case 0:
                System.out.println("Đã thoát chương trình. Tạm biệt!");
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại!");
        }
    }

    private static void addVehicleMenu(Scanner scanner, VehicleManager manager) {
        try {
            System.out.println("\n--- THÊM MỚI PHƯƠNG TIỆN ---");
            System.out.println("1. Thêm Car");
            System.out.println("2. Thêm Motorbike");
            System.out.println("3. Thêm Truck");
            System.out.print("Chọn loại xe (1-3): ");
            int type = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Nhập biển số xe (đúng 5 ký tự): ");
            String number = scanner.nextLine().trim();
            if (manager.findByVehicleNumber(number) != null) {
                System.out.println("=> Biển số xe đã tồn tại trong hệ thống!");
                return;
            }

            System.out.print("Nhập hãng (Honda, Yamaha, Toyota, Suzuki): ");
            String manufacturer = scanner.nextLine().trim();
            System.out.print("Nhập năm sản xuất (> 2000): ");
            int year = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Nhập màu xe: ");
            String color = scanner.nextLine().trim();

            System.out.print("Nhập CCCD chủ xe (đúng 12 số): ");
            String cccd = scanner.nextLine().trim();
            System.out.print("Nhập họ tên chủ xe: ");
            String name = scanner.nextLine().trim();
            System.out.print("Nhập email chủ xe: ");
            String email = scanner.nextLine().trim();

            Owner owner = new Owner(cccd, name, email);
            Vehicle vehicle;

            if (type == 1) {
                System.out.print("Nhập số chỗ ngồi: ");
                int seats = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Nhập kiểu động cơ: ");
                String engine = scanner.nextLine().trim();
                vehicle = new Car(number, manufacturer, year, color, owner, seats, engine);
            } else if (type == 2) {
                System.out.print("Nhập dung tích/công suất (cc): ");
                double capacity = Double.parseDouble(scanner.nextLine().trim());
                vehicle = new Motorbike(number, manufacturer, year, color, owner, capacity);
            } else if (type == 3) {
                System.out.print("Nhập trọng tải (tấn): ");
                double tonnage = Double.parseDouble(scanner.nextLine().trim());
                vehicle = new Truck(number, manufacturer, year, color, owner, tonnage);
            } else {
                System.out.println("Loại xe không hợp lệ!");
                return;
            }

            if (manager.addVehicle(vehicle)) {
                System.out.println("=> Thêm phương tiện thành công!");
            } else {
                System.out.println("=> Thêm phương tiện thất bại!");
            }
        } catch (Exception e) {
            System.out.println("=> Lỗi: " + e.getMessage());
        }
    }

    private static void searchByVehicleNumber(Scanner scanner, VehicleManager manager) {
        System.out.print("Nhập biển số xe cần tìm: ");
        String number = scanner.nextLine().trim();
        Vehicle v = manager.findByVehicleNumber(number);
        if (v != null) {
            System.out.println("=> Tìm thấy: " + v);
        } else {
            System.out.println("=> Không tìm thấy phương tiện với biển số: " + number);
        }
    }

    private static void searchByOwnerCccd(Scanner scanner, VehicleManager manager) {
        System.out.print("Nhập số CCCD cần tìm: ");
        String cccd = scanner.nextLine().trim();
        List<Vehicle> list = manager.findByOwnerCccd(cccd);
        if (list.isEmpty()) {
            System.out.println("=> Không tìm thấy phương tiện nào thuộc CCCD: " + cccd);
        } else {
            System.out.println("=> Danh sách phương tiện của CCCD " + cccd + ":");
            for (int i = 0; i < list.size(); i++) {
                System.out.println((i + 1) + ". " + list.get(i));
            }
        }
    }

    private static void deleteByManufacturer(Scanner scanner, VehicleManager manager) {
        System.out.print("Nhập hãng sản xuất cần xóa toàn bộ xe: ");
        String manufacturer = scanner.nextLine().trim();
        int deletedCount = manager.deleteByManufacturer(manufacturer);
        System.out.printf("=> Đã xóa %d phương tiện của hãng %s.%n", deletedCount, manufacturer);
    }

    private static void displayStatistics(VehicleManager manager) {
        System.out.println("\n--- BÁO CÁO THỐNG KÊ LOẠI PHƯƠNG TIỆN ---");
        Map<String, Long> stats = manager.getVehicleTypeStatistics();
        for (Map.Entry<String, Long> entry : stats.entrySet()) {
            System.out.printf("%-25s: %d%n", entry.getKey(), entry.getValue());
        }
    }
}
