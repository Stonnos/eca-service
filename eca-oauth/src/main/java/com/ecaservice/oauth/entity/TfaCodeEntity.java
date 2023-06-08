package com.ecaservice.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
     * Two - factor authentication code value
     */
    @Column(nullable = false)
    private String code;

    /**
     * Authentication serialized object in base64
     */
    @Column(columnDefinition = "text", nullable = false)
    private String authentication;
}
