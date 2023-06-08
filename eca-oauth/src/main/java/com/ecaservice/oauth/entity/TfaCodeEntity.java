package com.ecaservice.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
     * Authentication serialized object
     */
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] authentication;
}
