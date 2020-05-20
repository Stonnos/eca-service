package com.ecaservice.oauth.config;

import org.passay.PasswordGenerator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Eca oauth module configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties({CommonConfig.class, PasswordConfig.class})
public class EcaOauthConfiguration {

    /**
     * Creates password generator bean.
     *
     * @return password generator bean
     */
    @Bean
    public PasswordGenerator passwordGenerator() {
        return new PasswordGenerator();
    }
}
