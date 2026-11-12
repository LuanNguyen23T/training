package com.sun.training.employeemanagement.repository;

import com.sun.training.employeemanagement.entity.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for Employee entity.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Finds an employee by unique employee code.
     *
     * @param employeeCode unique employee code
     * @return optional containing employee if found
     */
    Optional<Employee> findByEmployeeCode(String employeeCode);

    /**
     * Finds an employee by unique email.
     *
     * @param email employee email
     * @return optional containing employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Checks if an employee with given employee code exists.
     *
     * @param employeeCode employee code
     * @return true if exists
     */
    boolean existsByEmployeeCode(String employeeCode);

    /**
     * Checks if an employee with given email exists.
     *
     * @param email employee email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds employees by department id.
     *
     * @param departmentId department identifier
     * @return list of employees in the department
     */
    List<Employee> findByDepartmentId(Long departmentId);

    /**
     * Finds employees by name containing keyword (case-insensitive).
     *
     * @param name keyword to search in employee name
     * @return list of matching employees
     */
    List<Employee> findByNameContainingIgnoreCase(String name);

    /**
     * Finds employees by department name (case-insensitive).
     *
     * @param departmentName name of the department
     * @return list of matching employees
     */
    List<Employee> findByDepartmentNameIgnoreCase(String departmentName);

    /**
     * Searches employees with optional department filter and optional keyword search.
     * Eagerly fetches department to avoid N+1 query problem.
     *
     * @param department optional department name filter
     * @param keyword optional keyword matching employee code, name, or email
     * @return list of matching employees
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department d WHERE "
            + "(:department IS NULL OR :department = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :department, '%'))) "
            + "AND (:keyword IS NULL OR :keyword = '' OR ("
            + "     LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "     OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "     OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%')))) "
            + "ORDER BY e.employeeCode ASC")
    List<Employee> searchEmployees(@Param("department") String department, @Param("keyword") String keyword);

    /**
     * Deletes an employee by employee code.
     *
     * @param employeeCode unique employee code
     */
    void deleteByEmployeeCode(String employeeCode);
}
