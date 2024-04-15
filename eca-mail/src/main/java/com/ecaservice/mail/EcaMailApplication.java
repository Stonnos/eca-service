package com.ecaservice.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Eca mail application main class.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
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
