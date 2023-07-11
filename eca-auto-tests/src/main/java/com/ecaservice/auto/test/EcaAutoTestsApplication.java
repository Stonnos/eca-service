package com.ecaservice.auto.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main class for auto tests application.
 *
 * @author Roman Batygin
 */
@EnableFeignClients
@SpringBootApplication
public class EcaAutoTestsApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaAutoTestsApplication.class, args);
    }

}
