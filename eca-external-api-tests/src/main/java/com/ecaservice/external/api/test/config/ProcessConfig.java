package com.ecaservice.external.api.test.config;

import com.ecaservice.external.api.test.dto.AutoTestType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

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
     * Process ids
     */
    @NotEmpty
    private Map<AutoTestType, String> ids;
}
