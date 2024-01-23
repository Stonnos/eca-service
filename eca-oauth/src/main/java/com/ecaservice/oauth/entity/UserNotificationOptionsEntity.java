package com.ecaservice.oauth.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * User profile notification options persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "user_notification_options", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_profile_options_id", "event_code"}, name =
                "user_profile_options_id_event_code_unique_idx")}
)
public class UserNotificationOptionsEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Notification event code
     */
    @Column(name = "event_code", nullable = false)
    private String eventCode;

    /**
     * Notification event title
     */
    @Column(name = "event_title", nullable = false)
    private String eventTitle;

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
