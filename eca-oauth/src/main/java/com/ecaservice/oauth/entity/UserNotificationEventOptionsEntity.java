package com.ecaservice.oauth.entity;

import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import lombok.Data;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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

    /**
     * User profile options
     */
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_profile_options_id", nullable = false)
    private UserProfileOptionsEntity userProfileOptions;
}
