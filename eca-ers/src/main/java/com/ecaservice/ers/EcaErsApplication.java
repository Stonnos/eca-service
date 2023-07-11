package com.ecaservice.ers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Evaluation results storage main class.
 *
 * @author Roman Batygin
 */
@EnableEurekaClient
@SpringBootApplication
public class EcaErsApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaErsApplication.class, args);
    }

}
