package com.ecaservice.web.push.config;

import com.ecaservice.web.push.entity.NotificationEventType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * User notification properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("user-notification")
public class UserNotificationProperties {

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
     * User profile notification properties
     */
    @Data
    public static class UserNotificationEventProperties {

        /**
         * Notification event type
         */
        private NotificationEventType eventType;

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
}
