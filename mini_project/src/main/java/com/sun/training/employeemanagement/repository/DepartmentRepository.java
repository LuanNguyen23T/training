package com.sun.training.employeemanagement.repository;

import com.sun.training.employeemanagement.dto.DepartmentStatisticDto;
import com.sun.training.employeemanagement.entity.Department;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for Department entity.
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Finds a department by its unique name.
     *
     * @param name department name
     * @return optional containing department if found
     */
    Optional<Department> findByName(String name);

    /**
     * Finds a department by its unique name, case-insensitively.
     *
     * @param name department name
     * @return optional containing department if found
     */
    Optional<Department> findByNameIgnoreCase(String name);

    /**
     * Checks if a department exists by name, case-insensitively.
     *
     * @param name department name
     * @return true if exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Finds all departments eagerly fetching associated employees to avoid LazyInitializationException.
     *
     * @return list of departments with employees initialized
     */
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();

    /**
     * Aggregates employee counts across all departments, including departments with 0 employees (Module 10).
     *
     * @return list of department statistics projection
     */
    @Query("SELECT new com.sun.training.employeemanagement.dto.DepartmentStatisticDto("
            + "d.name, "
            + "COUNT(e), "
            + "COALESCE(AVG(e.salary), 0.0)) "
            + "FROM Department d LEFT JOIN d.employees e "
            + "GROUP BY d.id, d.name "
            + "ORDER BY COUNT(e) DESC, d.name ASC")
    List<DepartmentStatisticDto> countEmployeesPerDepartment();
}
