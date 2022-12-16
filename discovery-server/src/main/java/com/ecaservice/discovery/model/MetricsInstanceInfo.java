package com.ecaservice.discovery.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Metrics instance info.
 *
 * @author Roman Batygin
 */
@Data
public class MetricsInstanceInfo {

    /**
     * Instance id
     */
    private String instanceId;

    /**
     * Instance host name
     */
    private String hostName;

    /**
     * Application name for instance
     */
    private String appName;

    /**
     * Instance ip address
     */
    private String ipAddress;

    /**
     * Instance port to get metrics
     */
    private int managementPort;

    /**
     * Instance status
     */
    private MetricsInstanceStatus status;

    /**
     * Last updated date
     */
    private LocalDateTime lastUpdatedDate;

    /**
     * Instance unique number per application name
     */
    private int instanceNumber;
}
