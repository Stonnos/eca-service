package com.ecaservice.oauth.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User profile options persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "user_profile_options")
public class UserProfileOptionsEntity {

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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile_options_id", nullable = false)
    private List<UserNotificationEventOptionsEntity> notificationEventOptions;

    /**
     * User entity
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false, unique = true)
    private UserEntity userEntity;
}
