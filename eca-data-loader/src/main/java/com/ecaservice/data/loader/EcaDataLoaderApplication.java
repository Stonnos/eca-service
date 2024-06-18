package com.ecaservice.data.loader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for Eca data loader application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
public class EcaDataLoaderApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaDataLoaderApplication.class, args);
    }
}
