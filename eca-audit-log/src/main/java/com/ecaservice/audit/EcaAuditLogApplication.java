package com.ecaservice.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for eca audit log application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
public class EcaAuditLogApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaAuditLogApplication.class, args);
    }
}
