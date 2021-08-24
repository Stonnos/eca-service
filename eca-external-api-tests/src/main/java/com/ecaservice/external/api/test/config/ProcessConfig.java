package com.ecaservice.external.api.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * BPM process config.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("process")
public class ProcessConfig {

    /**
     * Process id
     */
    @NotEmpty
    private String processId;
}
