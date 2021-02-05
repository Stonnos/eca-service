package com.ecaservice.notification.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
     * Template code
     */
    @NotBlank
    private String templateCode;

    /**
     * Email message variables
     */
    private Map<@NotBlank String, @NotBlank String> variables;
}
