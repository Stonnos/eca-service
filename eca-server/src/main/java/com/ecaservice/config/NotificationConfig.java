package com.ecaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Notification config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("notification")
public class NotificationConfig {

    /**
     * Web pushes enabled?
     */
   private Boolean webPushesEnabled;
}
