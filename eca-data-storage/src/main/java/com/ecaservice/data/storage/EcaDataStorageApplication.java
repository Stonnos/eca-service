package com.ecaservice.data.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main class for Eca data storage application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class EcaDataStorageApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaDataStorageApplication.class, args);
    }
}
