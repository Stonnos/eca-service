package com.ecaservice.oauth.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
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
     * User profile options version
     */
    @Column(nullable = false)
    private Integer version;

    /**
     * Notification options list
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userProfileOptions")
    @OrderBy("id")
    private List<UserNotificationEventOptionsEntity> notificationEventOptions;

    /**
     * User entity
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false, unique = true)
    private UserEntity userEntity;
}
