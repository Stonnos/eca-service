package com.ecaservice.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Eca mail application main class.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableEurekaClient
public class EcaMailApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaMailApplication.class, args);
    }

}
