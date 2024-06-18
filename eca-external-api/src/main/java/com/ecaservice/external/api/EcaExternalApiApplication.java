package com.ecaservice.external.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for Eca external API application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
public class EcaExternalApiApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaExternalApiApplication.class, args);
    }
}
