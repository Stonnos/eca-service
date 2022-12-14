package com.ecaservice.discovery.prometheus.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Prometheus service discovery config dto.
 *
 * @author Roman Batygin
 */
@Data
public class PrometheusSdConfig {

    /**
     * Target urls list to scrape metrics.
     */
    private List<String> targets;

    /**
     * Specific labels map.
     */
    private Map<String, String> labels;
}
