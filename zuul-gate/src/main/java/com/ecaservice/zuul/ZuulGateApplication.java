package com.ecaservice.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Main class for Zuul Gate application.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
//@EnableZuulProxy
@EnableEurekaClient
public class ZuulGateApplication {

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ZuulGateApplication.class, args);
    }
}
