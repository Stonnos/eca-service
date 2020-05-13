package com.notification.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.notification.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.notification.util.FieldConstraints.EMAIL_REGEX;
import static com.notification.util.FieldConstraints.SUBJECT_MAX_SIZE;

/**
 * Email request dto.
 */
@Data
public class EmailRequest {

    /**
     * Sender email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(max = EMAIL_MAX_SIZE)
    private String sender;

    /**
     * Receiver email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(max = EMAIL_MAX_SIZE)
    private String receiver;

    /**
     * Subject string
     */
    @NotBlank
    @Size(max = SUBJECT_MAX_SIZE)
    private String subject;

    /**
     * Email message
     */
    @NotBlank
    private String message;

    /**
     * Is html message?
     */
    private boolean html;
}
