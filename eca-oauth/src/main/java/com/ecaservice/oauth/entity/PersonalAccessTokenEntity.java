package com.ecaservice.oauth.entity;

import com.ecaservice.user.dto.PersonalAccessTokenType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Personal access token persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "personal_access_token",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"token_name", "user_id"},
                        name = "personal_access_token_token_name_user_id_unique_index")
        })
public class PersonalAccessTokenEntity extends TokenEntity {

    /**
     * Token name
     */
    @Column(name = "token_name", nullable = false, updatable = false)
    private String name;

    /**
     * Token type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private PersonalAccessTokenType tokenType;
}
