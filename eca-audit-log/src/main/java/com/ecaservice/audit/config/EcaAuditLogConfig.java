package com.ecaservice.audit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Eca audit log application properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("audit-log")
public class EcaAuditLogConfig {

    /**
     * Maximum page size for paging requests
     */
    private Integer maxPageSize;
}
