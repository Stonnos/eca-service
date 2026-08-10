package com.ecaservice.web.push.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification options persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "notification_options")
public class NotificationOptionsEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Email notifications enabled? (global flag)
     */
    @Column(name = "email_enabled")
    private boolean emailEnabled;

    /**
     * Web push notifications enabled? (global flag)
     */
    @Column(name = "web_push_enabled")
    private boolean webPushEnabled;

    /**
     * Created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Notification options list
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "notificationOptions")
    @OrderBy("id")
    private List<NotificationEventOptionsEntity> notificationEventOptions;

    /**
     * User login
     */
    @Column(name = "_user", nullable = false, unique = true)
    private String user;
}
