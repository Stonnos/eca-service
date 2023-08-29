package com.ecaservice.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * BPMN process config.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("processes")
public class ProcessConfig {

    /**
     * Process experiment diagram id
     */
    @NotEmpty
   private String processExperimentId;
}
