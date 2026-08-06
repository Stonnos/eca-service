package com.ecaservice.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Change email request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "change_email_request")
public class ChangeEmailRequestEntity extends TokenEntity {

    /**
     * Confirmation code value
     */
    @Column(name = "confirmation_code", nullable = false)
    private String confirmationCode;

    /**
     * Revocation token value
     */
    @Column(name = "revocation_token", nullable = false, unique = true)
    private String revocationToken;

    /**
     * Confirmation date
     */
    @Column(name = "confirmation_date")
    private LocalDateTime confirmationDate;

    /**
     * Revocation date
     */
    @Column(name = "revocation_date")
    private LocalDateTime revocationDate;

    /**
     * Revocation date
     */
    @Column(name = "revocation_expire_at", nullable = false)
    private LocalDateTime revocationExpireAt;

    /**
     * Old email
     */
    @Column(name = "old_email", nullable = false)
    private String oldEmail;

    /**
     * New email
     */
    @Column(name = "new_email", nullable = false)
    private String newEmail;
}
