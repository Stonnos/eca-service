package com.ecaservice.notification.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

import static com.ecaservice.notification.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.notification.util.FieldConstraints.EMAIL_REGEX;

/**
 * Email request dto.
 */
@Data
public class EmailRequest {

    /**
     * Receiver email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(max = EMAIL_MAX_SIZE)
    private String receiver;

    /**
     * Email type
     */
    @NotNull
    private EmailType templateType;

    /**
     * Email message variables
     */
    private Map<String, Object> emailMessageVariables;

    /**
     * Is html message?
     */
    private boolean html;
}
