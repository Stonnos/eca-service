package com.ecaservice.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

import static com.ecaservice.notification.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.notification.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.notification.util.Priority.HIGHEST;
import static com.ecaservice.notification.util.Priority.LOW;

/**
 * Email request dto.
 */
@Data
@Schema(description = "Email request")
public class EmailRequest {

    /**
     * Receiver email
     */
    @NotBlank
    @Email(regexp = EMAIL_REGEX)
    @Size(max = EMAIL_MAX_SIZE)
    @Schema(description = "Receiver email", example = "bat1238@yandex.ru", required = true)
    private String receiver;

    /**
     * Template code
     */
    @NotBlank
    @Schema(description = "Email template code", example = "NEW_EXPERIMENT", required = true)
    private String templateCode;

    /**
     * Email message variables
     */
    @Schema(description = "Email templates variables")
    private Map<@NotBlank String, @NotBlank String> variables;

    /**
     * Delivery priority
     */
    @NotNull
    @Min(LOW)
    @Max(HIGHEST)
    @Schema(description = "Delivery priority", example = "0")
    private Integer priority;
}
