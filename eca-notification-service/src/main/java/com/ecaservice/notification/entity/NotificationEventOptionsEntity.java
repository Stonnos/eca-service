package com.ecaservice.notification.entity;

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
import lombok.Data;
import lombok.ToString;

/**
 * Notification event options persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "notification_event_options", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"notification_options_id", "event_type"}, name =
                "notification_options_id_event_type_unique_idx")}
)
public class NotificationEventOptionsEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Notification event type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private NotificationEventType eventType;

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
     * Notification options
     */
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "notification_options_id", nullable = false)
    private NotificationOptionsEntity notificationOptions;
}
