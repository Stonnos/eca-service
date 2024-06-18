package com.ecaservice.core.tracing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Zipkin report properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("management.zipkin.tracing")
public class ZipkinReportProperties {

    /**
     * Zipkin report enabled?
     */
    public boolean enabled;
}
