package com.ecaservice.server;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main class.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableProcessApplication
@EnableEurekaClient
@EnableFeignClients
public class EcaApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaApplication.class, args);
    }

}
