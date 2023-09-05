package com.ecaservice.feign.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Feign oauth properties.
 *
 * @author Roman
 */
@Data
@ConfigurationProperties("service.feign.oauth")
public class FeignOauthProperties {

    /**
     * Token base url
     */
    private String tokenUrl;

    /**
     * Client id
     */
    private String clientId;

    /**
     * Client secret
     */
    private String clientSecret;
}
