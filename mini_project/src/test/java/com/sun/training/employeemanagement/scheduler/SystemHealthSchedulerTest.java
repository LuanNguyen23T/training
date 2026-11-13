package com.sun.training.employeemanagement.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SystemHealthScheduler scheduled tasks (Module 8).
 */
class SystemHealthSchedulerTest {

    private final SystemHealthScheduler scheduler = new SystemHealthScheduler();

    @Test
    @DisplayName("Module 8: SystemHealthScheduler reportSystemRunning executes without throwing exception")
    void shouldReportSystemRunningWithoutError() {
        assertDoesNotThrow(scheduler::reportSystemRunning);
    }
}
