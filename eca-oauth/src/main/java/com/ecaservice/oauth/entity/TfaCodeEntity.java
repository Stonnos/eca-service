package com.ecaservice.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Two - factor authentication code persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "tfa_code")
public class TfaCodeEntity extends TokenEntity {

    /**
     * Code confirmation date
     */
    @Column(name = "confirmation_date")
    private LocalDateTime confirmationDate;
}
