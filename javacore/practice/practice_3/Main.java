package javacore.practice.practice_3;

public class Main {
    public static void main(String[] args) {
        VehicleManager manager = new VehicleManager();
        initSampleData(manager);
        ConsoleUI.start(manager);
    }

    private static void initSampleData(VehicleManager manager) {
        Owner o1 = new Owner("079201001234", "Nguyễn Văn A", "nguyenvana@gmail.com");
        Owner o2 = new Owner("079201005678", "Trần Thị B", "tranthib@gmail.com");
        Owner o3 = new Owner("079201009999", "Lê Văn C", "levanc@gmail.com");

        manager.addVehicle(new Car("29A12", "Toyota", 2022, "Trắng", o1, 5, "Xăng"));
        manager.addVehicle(new Car("30B45", "Honda", 2023, "Đen", o2, 7, "Xăng"));
        manager.addVehicle(new Motorbike("59C78", "Honda", 2021, "Đỏ", o1, 150.0));
        manager.addVehicle(new Motorbike("43D90", "Yamaha", 2024, "Xanh", o3, 125.0));
        manager.addVehicle(new Truck("51E11", "Suzuki", 2020, "Trắng", o2, 3.5));
    }
}
