package com.ecaservice.external.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Main class for Eca external API application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableEurekaClient
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
