package javacore.practice.practice_3;

import java.util.*;
import java.util.stream.Collectors;

public class VehicleManager {
    private final List<Vehicle> vehicles = new ArrayList<>();

    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || findByVehicleNumber(vehicle.getVehicleNumber()) != null) {
            return false;
        }
        return vehicles.add(vehicle);
    }

    public Vehicle findByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) return null;
        return vehicles.stream()
                .filter(v -> v.getVehicleNumber().equalsIgnoreCase(vehicleNumber.trim()))
                .findFirst()
                .orElse(null);
    }

    public List<Vehicle> findByOwnerCccd(String cccd) {
        if (cccd == null) return List.of();
        return vehicles.stream()
                .filter(v -> v.getOwner().getCccd().equalsIgnoreCase(cccd.trim()))
                .collect(Collectors.toList());
    }

    public int deleteByManufacturer(String manufacturer) {
        if (manufacturer == null) return 0;
        int before = vehicles.size();
        vehicles.removeIf(v -> v.getManufacturer().equalsIgnoreCase(manufacturer.trim()));
        return before - vehicles.size();
    }

    public String getManufacturerWithMostVehicles() {
        if (vehicles.isEmpty()) return "Không có phương tiện nào trong danh sách";

        Map<String, Long> countMap = vehicles.stream()
                .collect(Collectors.groupingBy(Vehicle::getManufacturer, Collectors.counting()));

        long maxCount = Collections.max(countMap.values());
        return countMap.entrySet().stream()
                .filter(e -> e.getValue() == maxCount)
                .map(e -> e.getKey() + " (" + e.getValue() + " xe)")
                .collect(Collectors.joining(", "));
    }

    public void sortByVehicleNumberDescending() {
        vehicles.sort(Comparator.comparing(Vehicle::getVehicleNumber, String.CASE_INSENSITIVE_ORDER).reversed());
    }

    public Map<String, Long> getVehicleTypeStatistics() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("Car", vehicles.stream().filter(v -> v instanceof Car).count());
        stats.put("Motorbike", vehicles.stream().filter(v -> v instanceof Motorbike).count());
        stats.put("Truck", vehicles.stream().filter(v -> v instanceof Truck).count());
        stats.put("Tổng số phương tiện", (long) vehicles.size());
        return stats;
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void displayAll() {
        if (vehicles.isEmpty()) {
            System.out.println("Danh sách phương tiện đang rỗng!");
            return;
        }
        for (int i = 0; i < vehicles.size(); i++) {
            System.out.println((i + 1) + ". " + vehicles.get(i));
        }
    }
}
