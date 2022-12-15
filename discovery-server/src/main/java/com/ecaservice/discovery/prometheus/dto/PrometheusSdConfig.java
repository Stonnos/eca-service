package com.ecaservice.discovery.prometheus.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

import static com.ecaservice.discovery.prometheus.dto.FieldConstraints.MAX_LENGTH_255;

/**
 * Prometheus service discovery config dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Prometheus service discovery config")
public class PrometheusSdConfig {

    /**
     * Target urls list to scrape metrics.
     */
    @ArraySchema(schema = @Schema(description = "Target url", maxLength = MAX_LENGTH_255))
    private List<String> targets;

    /**
     * Specific labels map.
     */
    @Schema(description = "Labels")
    private Map<String, String> labels;
}
