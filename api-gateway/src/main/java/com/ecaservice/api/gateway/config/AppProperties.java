package com.ecaservice.api.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("app")
public class AppProperties {

    private static final List<String> DEFAULT_BLACKLIST_URLS = List.of("/eca-oauth/oauth2/**");

    /**
     * Blacklist url for authentication header filer
     */
    private List<String> authHeaderFilterBlacklistUrls = DEFAULT_BLACKLIST_URLS;
}
