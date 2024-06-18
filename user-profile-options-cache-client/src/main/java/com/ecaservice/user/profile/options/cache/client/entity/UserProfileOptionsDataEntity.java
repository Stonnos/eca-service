package com.ecaservice.user.profile.options.cache.client.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import static com.ecaservice.user.profile.options.cache.client.util.FieldConstraints.USER_PROFILE_OPTIONS_JSON_MAX_LENGTH;

/**
 * User profile options data persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "user_profile_options_data")
public class UserProfileOptionsDataEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * User login
     */
    @Column(name = "_user", nullable = false)
    private String user;

    /**
     * Created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Updated date
     */
    @Column(nullable = false)
    private LocalDateTime updated;

    /**
     * User profile options version
     */
    @Column(nullable = false)
    private Integer version;

    /**
     * User profile options json string
     */
    @Column(name = "options_json", nullable = false, length = USER_PROFILE_OPTIONS_JSON_MAX_LENGTH)
    private String optionsJson;
}
