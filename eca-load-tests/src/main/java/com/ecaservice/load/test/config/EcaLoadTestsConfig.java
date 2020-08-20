package com.ecaservice.load.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Eca load tests config model.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("eca-load-tests")
public class EcaLoadTestsConfig {
}
