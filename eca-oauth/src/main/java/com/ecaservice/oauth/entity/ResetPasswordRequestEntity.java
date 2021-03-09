package com.ecaservice.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Reset password request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "reset_password_request")
public class ResetPasswordRequestEntity extends TokenEntity {

    /**
     * Password reset date
     */
    @Column(name = "reset_date")
    private LocalDateTime resetDate;
}
