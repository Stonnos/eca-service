package com.ecaservice.external.api.test;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main class for external api tests application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableProcessApplication
@EnableFeignClients
public class ExternalApiTestsApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ExternalApiTestsApplication.class);
    }

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ExternalApiTestsApplication.class, args);
    }

}
