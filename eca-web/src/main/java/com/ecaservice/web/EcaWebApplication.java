package com.ecaservice.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Main class for web application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableEurekaClient
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
