package com.ecaservice.data.loader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Eca data loader properties.
 */
@Data
@ConfigurationProperties("app")
public class EcaDataLoaderProperties {

    /**
     * Temporary files location on file system
     */
    private String tempFilesLocation;
}
