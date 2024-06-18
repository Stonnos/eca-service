package com.ecaservice.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Resource server properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("resource.server")
public class ResourceServerProperties {

    /**
     * Security properties
     */
    private SecurityProperties security = new SecurityProperties();

    /**
     * Security properties.
     */
    @Data
    public static class SecurityProperties {

        /**
         * Allow bearer token uri query parameter?
         */
        private boolean allowUBearerTokenUriQueryParameter;

        /**
         * Whitelist urls
         */
        private List<String> whitelistUrls = newArrayList();
    }
}
