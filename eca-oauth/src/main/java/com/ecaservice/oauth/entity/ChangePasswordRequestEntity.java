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
     * Approve date
     */
    @Column(name = "approve_date")
    private LocalDateTime approveDate;

    /**
     * New password
     */
    @Column(name = "new_password", nullable = false)
    private String newPassword;
}
