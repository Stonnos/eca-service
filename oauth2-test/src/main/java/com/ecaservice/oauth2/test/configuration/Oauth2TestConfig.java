package com.ecaservice.oauth2.test.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

/**
 * Oauth2 test config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("oauth2-test")
public class Oauth2TestConfig {

    /**
     * Username
     */
    private String username = "admin";

    /**
     * Scope
     */
    private List<String> scope = List.of("web", "internal-api");
}