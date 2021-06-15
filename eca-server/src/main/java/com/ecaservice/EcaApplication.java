package com.ecaservice;

import com.ecaservice.service.auth.UsersClient;
import com.ecaservice.service.ers.ErsClient;
import com.ecaservice.service.experiment.mail.EmailClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main class.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackageClasses = {ErsClient.class, UsersClient.class, EmailClient.class})
public class EcaApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EcaApplication.class);
    }

    /**
     * Runs application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcaApplication.class, args);
    }

}
