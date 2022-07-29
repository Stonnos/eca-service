package com.ecaservice.web.push.config.ws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * Queues properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("queues")
public class QueueConfig {

    /**
     * Bindings map
     */
    @NotEmpty
    private Map<@NotBlank String, @NotBlank String> bindings;
}
