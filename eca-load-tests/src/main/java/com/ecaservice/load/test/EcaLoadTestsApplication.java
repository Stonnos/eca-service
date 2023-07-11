package com.ecaservice.load.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for load tests application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
public class EcaLoadTestsApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaLoadTestsApplication.class, args);
    }

}
