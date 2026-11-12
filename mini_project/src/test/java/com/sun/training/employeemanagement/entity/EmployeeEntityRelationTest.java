package com.sun.training.employeemanagement.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class EmployeeEntityRelationTest {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    EmployeeEntityRelationTest(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Test
    void testDepartmentAndEmployeeRelation() {
        Department dept = new Department("Engineering", "Software Development Division");
        Department savedDept = departmentRepository.save(dept);
        assertNotNull(savedDept.getId(), "Department ID should be generated");

        Employee emp = new Employee(
                "N0001",
                "Nguyen Huu Hoang Luan",
                "luan.nguyen@sun-asterisk.com",
                15000000.0,
                savedDept
        );
        Employee savedEmp = employeeRepository.save(emp);
        assertNotNull(savedEmp.getId(), "Employee ID should be generated");
        assertNotNull(savedEmp.getDepartment(), "Employee should reference Department");
        assertEquals(savedDept.getId(), savedEmp.getDepartment().getId(), "Department ID should match");

        Optional<Employee> fetchedOpt = employeeRepository.findByEmployeeCode("N0001");
        assertTrue(fetchedOpt.isPresent(), "Should find employee by code");
        Employee fetchedEmp = fetchedOpt.get();
        assertEquals("Nguyen Huu Hoang Luan", fetchedEmp.getName());
        assertEquals("Engineering", fetchedEmp.getDepartment().getName());

        List<Employee> deptEmployees = employeeRepository.findByDepartmentId(savedDept.getId());
        assertEquals(1, deptEmployees.size(), "Department should contain 1 employee");
        assertEquals("N0001", deptEmployees.get(0).getEmployeeCode());
    }
}

