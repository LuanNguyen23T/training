package com.sun.training.employeemanagement.repository;

import com.sun.training.employeemanagement.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for User entity operations (Module 9).
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by unique username.
     *
     * @param username the username to look up
     * @return optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user already exists with the given username.
     *
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
}
