package com.ecaservice.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Force set password request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "force_set_password_request")
public class ForceSetPasswordRequestEntity extends TokenEntity {

    /**
     * Confirmation code value
     */
    @Column(name = "confirmation_code", nullable = false)
    private String confirmationCode;

    /**
     * Password set date
     */
    @Column(name = "password_date")
    private LocalDateTime passwordDate;
}
