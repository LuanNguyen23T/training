package com.sun.training.employeemanagement.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks component for system background operations (Module 8).
 * Executes scheduled health heartbeats and maintenance jobs.
 */
@Component
public class SystemHealthScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemHealthScheduler.class);

    /**
     * Periodic scheduled task running every 30 seconds to log system running heartbeat.
     * fixedRate: 30000ms (30 seconds)
     */
    @Scheduled(fixedRate = 30000)
    public void reportSystemRunning() {
        LOGGER.info("System running");
    }
}

