package com.ecaservice.oauth.entity;

import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * User profile notification event options persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "user_notification_event_options", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_profile_options_id", "event_type"}, name =
                "user_profile_options_id_event_type_unique_idx")}
)
public class UserNotificationEventOptionsEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Notification event type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private UserNotificationEventType eventType;

    /**
     * Email notifications enabled?
     */
    @Column(name = "email_enabled")
    private boolean emailEnabled;

    /**
     * Web push notifications enabled?
     */
    @Column(name = "web_push_enabled")
    private boolean webPushEnabled;

    /**
     * Email notifications supported?
     */
    @Column(name = "email_supported")
    private boolean emailSupported;

    /**
     * Web push notifications supported?
     */
    @Column(name = "web_push_supported")
    private boolean webPushSupported;
}
