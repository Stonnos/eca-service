package com.ecaservice.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main class for discovery server application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
