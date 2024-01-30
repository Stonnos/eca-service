package com.ecaservice.user.profile.options.cache.client.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

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
    @Column(nullable = false)
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
