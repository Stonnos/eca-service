package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Password config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("password")
public class PasswordConfig {

    /**
     * Password length
     */
    private Integer length;
}
