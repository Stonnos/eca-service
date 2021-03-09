package com.ecaservice.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
