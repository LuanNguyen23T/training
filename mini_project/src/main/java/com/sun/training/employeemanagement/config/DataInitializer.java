package com.sun.training.employeemanagement.config;

import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.entity.Employee;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes default sample data in the database on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Constructor injection for repositories.
     *
     * @param departmentRepository department repository bean
     * @param employeeRepository employee repository bean
     */
    public DataInitializer(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            LOGGER.info("Seeding initial department and employee data into database...");

            Department engineering = departmentRepository.save(
                    new Department("Engineering", "Software Engineering Department"));
            Department hr = departmentRepository.save(
                    new Department("Human Resources", "People and Culture Department"));
            Department finance = departmentRepository.save(
                    new Department("Finance", "Finance and Accounting Department"));

            List<Employee> initialEmployees = List.of(
                    new Employee("N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", 15000000.0, engineering),
                    new Employee("N0002", "Tran Thi Binh", "binh.tran@sun-asterisk.com", 12000000.0, hr),
                    new Employee("N0003", "Le Van Cuong", "cuong.le@sun-asterisk.com", 14000000.0, finance)
            );

            employeeRepository.saveAll(initialEmployees);
            LOGGER.info("Seeded 3 departments and {} employees into database.", initialEmployees.size());
        }
    }
}

