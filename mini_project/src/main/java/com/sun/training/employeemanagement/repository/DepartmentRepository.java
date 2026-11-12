package com.sun.training.employeemanagement.repository;

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
}
