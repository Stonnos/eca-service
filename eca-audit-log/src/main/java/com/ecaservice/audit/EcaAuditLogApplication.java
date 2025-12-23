package com.ecaservice.audit;

import com.ecaservice.audit.config.HintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Main class for eca audit log application.
 *
 * @author Roman Batygin
 */
@ImportRuntimeHints(HintsRegistrar.class)
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
