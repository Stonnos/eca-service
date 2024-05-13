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
     * Whitelist secured urls
     */
    private List<String> whitelistSecuredUrls = newArrayList();
}
