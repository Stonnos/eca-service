package com.ecaservice.oauth.config;

import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * User profile properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("user-profile")
public class UserProfileProperties {

    /**
     * Email notifications enabled? (global flag)
     */
    private boolean emailEnabled;

    /**
     * Web push notifications enabled? (global flag)
     */
    private boolean webPushEnabled;

    /**
     * Notifications event options list
     */
    private List<UserNotificationEventProperties> notificationEventOptions;

    /**
     * Data event retry interval seconds
     */
    private Integer dataEventRetryIntervalSeconds;

    /**
     * Rabbit properties
     */
    private RabbitProperties rabbit = new RabbitProperties();

    /**
     * User profile notification properties
     */
    @Data
    public static class UserNotificationEventProperties {

        /**
         * Notification event type
         */
        private UserNotificationEventType eventType;

        /**
         * Email notifications enabled?
         */
        private boolean emailEnabled;

        /**
         * Web push notifications enabled?
         */
        private boolean webPushEnabled;

        /**
         * Email notifications supported?
         */
        private boolean emailSupported;

        /**
         * Web push notifications supported?
         */
        private boolean webPushSupported;
    }

    /**
     * Rabbit properties.
     */
    @Data
    public static class RabbitProperties {

        /**
         * Exchange name
         */
        private String exchangeName;
    }
}
