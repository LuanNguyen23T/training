package com.sun.training.employeemanagement.service;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Utility service providing common helper methods such as string formatting and employee code generation.
 */
@Service
public class UtilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilityService.class);

    private final AtomicLong employeeCodeCounter = new AtomicLong(1);

    /**
     * Formats a raw person name into standard Title Case format.
     *
     * @param rawName raw input name
     * @return normalized title-cased name, or empty string if input is blank
     */
    public String formatFullName(String rawName) {
        LOGGER.info("Formatting name: '{}'", rawName);
        if (rawName == null || rawName.isBlank()) {
            return "";
        }

        return Arrays.stream(rawName.trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT)
                        + word.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }

    /**
     * Generates a unique sequential employee code in format NXXXX (e.g., N0001).
     *
     * @return formatted employee code
     */
    public String generateEmployeeCode() {
        long currentSequence = employeeCodeCounter.getAndIncrement();
        String code = String.format(Locale.ROOT, "N%04d", currentSequence);
        LOGGER.info("Generated employee code: {}", code);
        return code;
    }
}

