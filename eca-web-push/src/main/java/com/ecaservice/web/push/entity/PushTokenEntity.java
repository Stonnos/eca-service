package com.ecaservice.web.push.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Push token persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "push_token")
public class PushTokenEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * User
     */
    @Column(name = "user_name", nullable = false, unique = true)
    private String user;

    /**
     * Token value
     */
    @Column(name = "token_id", nullable = false)
    private String tokenId;

    /**
     * Token expiration date
     */
    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    /**
     * Validates token expiration.
     *
     * @return {@code true} if token is expired, otherwise {@code false}
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireAt);
    }
}
