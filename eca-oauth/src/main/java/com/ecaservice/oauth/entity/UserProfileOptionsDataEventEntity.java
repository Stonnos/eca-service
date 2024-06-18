package com.ecaservice.oauth.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import static com.ecaservice.oauth.util.FieldConstraints.USER_PROFILE_OPTIONS_DATA_EVENT_MESSAGE_BODY_MAX_SIZE;

/**
 * User profile data event persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "user_profile_options_data_event")
public class UserProfileOptionsDataEventEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request id
     */
    @Column(name = "request_id", nullable = false)
    private String requestId;

    /**
     * Message body
     */
    @Column(name = "message_body", nullable = false, length = USER_PROFILE_OPTIONS_DATA_EVENT_MESSAGE_BODY_MAX_SIZE)
    private String messageBody;

    /**
     * Creation date
     */
    @Column(nullable = false)
    private LocalDateTime created;
}
