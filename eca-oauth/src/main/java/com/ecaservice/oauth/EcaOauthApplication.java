package com.ecaservice.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main class for Oauth application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class EcaOauthApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaOauthApplication.class, args);
    }
}
