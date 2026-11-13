package com.sun.training.employeemanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * Integration tests for Caching functionality (Module 8).
 * Verifies cache hit on total employee count and cache eviction on employee creation.
 */
@SpringBootTest
@ActiveProfiles("dev")
class EmployeeCacheIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoSpyBean
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Module 8: Calling getTotalEmployeeCount repeatedly should return cached result and hit DB only once")
    void shouldCacheTotalEmployeeCount() {
        // Clear cache before test
        Cache cache = cacheManager.getCache("employeeTotalCount");
        if (cache != null) {
            cache.clear();
        }

        // First call - should hit DB (CACHE MISS)
        long firstCount = employeeService.getTotalEmployeeCount();

        // Second and third calls - should return from cache (CACHE HIT)
        long secondCount = employeeService.getTotalEmployeeCount();
        long thirdCount = employeeService.getTotalEmployeeCount();

        assertEquals(firstCount, secondCount);
        assertEquals(firstCount, thirdCount);

        // Verify count() on repository was called only once despite 3 service calls
        verify(employeeRepository, times(1)).count();
        assertNotNull(cache);
        assertNotNull(cache.get("totalCount"));
    }

    @Test
    @DisplayName("Module 8: Creating employee should evict employeeTotalCount cache")
    void shouldEvictCacheWhenEmployeeCreated() {
        Cache cache = cacheManager.getCache("employeeTotalCount");
        assertNotNull(cache);

        // Populate cache
        employeeService.getTotalEmployeeCount();
        assertNotNull(cache.get("totalCount"));

        // Create new employee
        EmployeeRequestDto request = new EmployeeRequestDto();
        request.setFullName("Cache Test User");
        request.setEmail("cache.test." + System.currentTimeMillis() + "@sun-asterisk.com");
        request.setDepartment("IT");
        request.setSalary(15000000.0);

        employeeService.createEmployee(request);

        // Verify cache was evicted
        assertNull(cache.get("totalCount"));
    }
}
