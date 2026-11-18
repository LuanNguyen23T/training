package com.sun.training.employeemanagement.config;

import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.entity.Employee;
import com.sun.training.employeemanagement.entity.Role;
import com.sun.training.employeemanagement.entity.User;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import com.sun.training.employeemanagement.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes default sample data in the database on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor injection for repositories and password encoder.
     *
     * @param departmentRepository department repository bean
     * @param employeeRepository employee repository bean
     * @param userRepository user repository bean
     * @param passwordEncoder password encoder bean
     */
    public DataInitializer(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.findByName("Engineering").isEmpty()) {
            departmentRepository.save(
                    new Department("Engineering", "Software Engineering Department"));
        }
        if (departmentRepository.findByName("IT").isEmpty()) {
            departmentRepository.save(
                    new Department("IT", "Information Technology and Infrastructure"));
        }
        if (departmentRepository.findByName("Human Resources").isEmpty()) {
            departmentRepository.save(
                    new Department("Human Resources", "People and Culture Department"));
        }
        if (departmentRepository.findByName("Finance").isEmpty()) {
            departmentRepository.save(
                    new Department("Finance", "Finance and Accounting Department"));
        }

        Department engineering = departmentRepository.findByName("Engineering").orElse(null);
        Department it = departmentRepository.findByName("IT").orElse(null);
        Department hr = departmentRepository.findByName("Human Resources").orElse(null);
        Department finance = departmentRepository.findByName("Finance").orElse(null);

        if (engineering != null && it != null && hr != null && finance != null) {
            List<Employee> allEmployees = List.of(
                    // 11 Nhân viên phòng Kỹ thuật (Engineering)
                    new Employee("N0001", "Nguyễn Hữu Hoàng Luân", "luan.nguyen@sun-asterisk.com", 28000000.0, engineering),
                    new Employee("N0004", "Lê Minh Tuấn", "tuan.le@sun-asterisk.com", 25000000.0, engineering),
                    new Employee("N0005", "Phạm Đức Thắng", "thang.pham@sun-asterisk.com", 32000000.0, engineering),
                    new Employee("N0006", "Đặng Hoàng Nam", "nam.dang@sun-asterisk.com", 22000000.0, engineering),
                    new Employee("N0007", "Vũ Trọng Nghĩa", "nghia.vu@sun-asterisk.com", 35000000.0, engineering),
                    new Employee("N0008", "Hoàng Quốc Bảo", "bao.hoang@sun-asterisk.com", 20000000.0, engineering),
                    new Employee("N0009", "Nguyễn Đình Trọng", "trong.nguyen@sun-asterisk.com", 27000000.0, engineering),
                    new Employee("N0010", "Đỗ Văn Hùng", "hung.do@sun-asterisk.com", 30000000.0, engineering),
                    new Employee("N0011", "Bùi Thanh Tùng", "tung.bui@sun-asterisk.com", 24000000.0, engineering),
                    new Employee("N0012", "Phan Văn Hải", "hai.phan@sun-asterisk.com", 38000000.0, engineering),
                    new Employee("N0026", "Phan Quốc Đạt", "dat.phan@sun-asterisk.com", 31000000.0, engineering),

                    // 8 Nhân viên phòng Công nghệ thông tin (IT)
                    new Employee("N0016", "Đỗ Thị Kim Ngân", "ngan.do@sun-asterisk.com", 23000000.0, it),
                    new Employee("N0017", "Hoàng Thị Lan Hương", "huong.hoang@sun-asterisk.com", 19000000.0, it),
                    new Employee("N0018", "Võ Thùy Linh", "linh.vo@sun-asterisk.com", 14000000.0, it),
                    new Employee("N0019", "Đinh Thị Thanh Thảo", "thao.dinh@sun-asterisk.com", 17000000.0, it),
                    new Employee("N0020", "Bùi Phương Thảo", "thao.bui@sun-asterisk.com", 20000000.0, it),
                    new Employee("N0021", "Nguyễn Thị Bích Ngọc", "ngoc.nguyen@sun-asterisk.com", 25000000.0, it),
                    new Employee("N0028", "Trịnh Minh Trí", "tri.trinh@sun-asterisk.com", 28000000.0, it),
                    new Employee("N0029", "Nguyễn Đức Toàn", "toan.nguyen@sun-asterisk.com", 23000000.0, it),

                    // 7 Nhân viên phòng Tài chính (Finance)
                    new Employee("N0003", "Lê Văn Cường", "cuong.le@sun-asterisk.com", 22000000.0, finance),
                    new Employee("N0022", "Nguyễn Hoàng Long", "long.nguyen@sun-asterisk.com", 26000000.0, finance),
                    new Employee("N0023", "Trần Quang Huy", "huy.tran@sun-asterisk.com", 20000000.0, finance),
                    new Employee("N0024", "Vũ Thị Mai Anh", "anh.vu@sun-asterisk.com", 19000000.0, finance),
                    new Employee("N0025", "Đặng Thu Thủy", "thuy.dang@sun-asterisk.com", 24000000.0, finance),
                    new Employee("N0027", "Lý Thanh Hằng", "hang.ly@sun-asterisk.com", 18000000.0, finance),
                    new Employee("N0030", "Phạm Quỳnh Nga", "nga.pham@sun-asterisk.com", 33000000.0, finance),

                    // 4 Nhân viên phòng Nhân sự (Human Resources)
                    new Employee("N0002", "Trần Thị Bình", "binh.tran@sun-asterisk.com", 18000000.0, hr),
                    new Employee("N0013", "Nguyễn Thị Mai", "mai.nguyen@sun-asterisk.com", 16000000.0, hr),
                    new Employee("N0014", "Lê Thị Thu Hà", "ha.le@sun-asterisk.com", 21000000.0, hr),
                    new Employee("N0015", "Phạm Ngọc Ánh", "anh.pham@sun-asterisk.com", 15000000.0, hr)
            );

            int addedCount = 0;
            int updatedCount = 0;
            for (Employee emp : allEmployees) {
                Optional<Employee> existingOpt = employeeRepository.findByEmployeeCode(emp.getEmployeeCode());
                if (existingOpt.isEmpty()) {
                    employeeRepository.save(emp);
                    addedCount++;
                } else {
                    Employee existing = existingOpt.get();
                    if (existing.getDepartment() == null
                            || !existing.getDepartment().getId().equals(emp.getDepartment().getId())) {
                        existing.setDepartment(emp.getDepartment());
                        employeeRepository.save(existing);
                        updatedCount++;
                    }
                }
            }
            if (addedCount > 0 || updatedCount > 0) {
                LOGGER.info("DataInitializer: Added {} new employees, updated {} employee departments. Total employees: {}",
                        addedCount, updatedCount, employeeRepository.count());
            }
        }

        if (userRepository.count() == 0) {
            LOGGER.info("Seeding initial security users into database (Module 9)...");

            User admin = new User("admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN);
            User user = new User("user", passwordEncoder.encode("user123"), Role.ROLE_USER);

            userRepository.saveAll(List.of(admin, user));
            LOGGER.info("Seeded default users: admin (ROLE_ADMIN) and user (ROLE_USER)");
        }
    }
}
