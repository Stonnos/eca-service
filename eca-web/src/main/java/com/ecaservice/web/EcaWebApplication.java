package com.ecaservice.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for web application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
public class EcaWebApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaWebApplication.class, args);
    }
}
