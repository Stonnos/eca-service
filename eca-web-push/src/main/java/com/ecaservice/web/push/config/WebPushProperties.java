package com.ecaservice.web.push.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("web-push")
public class WebPushProperties {

    /**
     * User notification life time in days
     */
    @NotNull
    private Integer notificationLifeTimeDays;

    /**
     * Push token validity time in minutes
     */
    @NotNull
    private Integer pushTokenValidityMinutes;

    /**
     * Rabbit properties
     */
    private RabbitProperties rabbit = new RabbitProperties();
}
