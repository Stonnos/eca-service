package com.ecaservice.oauth.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Reset password request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "reset_password_request")
public class ResetPasswordRequestEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Reset password token
     */
    @Column(nullable = false)
    private String token;

    /**
     * Reset password request expire date
     */
    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    /**
     * Password reset date
     */
    @Column(name = "reset_date")
    private LocalDateTime resetDate;

    /**
     * Linked user
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;
}
