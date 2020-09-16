package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TFA config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("tfa")
public class TfaConfig {

    /**
     * TFA code validity in seconds
     */
    private Long codeValiditySeconds;

    /**
     * TFA code length
     */
    private Integer codeLength;
}
