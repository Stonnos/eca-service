package com.ecaservice.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Change password request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "change_password_request")
public class ChangePasswordRequestEntity extends TokenEntity {

    /**
     * Confirmation code value
     */
    @Column(name = "confirmation_code", nullable = false)
    private String confirmationCode;

    /**
     * Confirmation date
     */
    @Column(name = "confirmation_date")
    private LocalDateTime confirmationDate;

    /**
     * New password
     */
    @Column(name = "new_password", nullable = false)
    private String newPassword;
}
