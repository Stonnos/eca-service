package com.ecaservice.oauth.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
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
     * Created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

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
