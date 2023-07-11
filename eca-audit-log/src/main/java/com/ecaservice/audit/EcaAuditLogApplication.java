package com.ecaservice.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Main class for eca audit log application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableEurekaClient
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
