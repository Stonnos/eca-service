package com.ecaservice.oauth.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Token persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@MappedSuperclass
public abstract class TokenEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Token value
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Token expire date
     */
    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    /**
     * Linked user
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;
}
