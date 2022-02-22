package com.ecaservice.notification.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

import static com.ecaservice.notification.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.notification.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.notification.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.notification.util.FieldConstraints.MAX_VARIABLES_SIZE;
import static com.ecaservice.notification.util.FieldConstraints.UUID_MAX_SIZE;
import static com.ecaservice.notification.util.FieldConstraints.UUID_PATTERN;
import static com.ecaservice.notification.util.FieldConstraints.VALUE_1;
import static com.ecaservice.notification.util.Priority.HIGHEST;
import static com.ecaservice.notification.util.Priority.LOW;

/**
 * Email request dto.
 */
@Data
@Schema(description = "Email request")
public class EmailRequest {

    /**
     * Request id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = VALUE_1, max = UUID_MAX_SIZE)
    @Schema(description = "Request id", example = "1d2de514-3a87-4620-9b97-c260e24340de", required = true)
    private String requestId;

    /**
     * Receiver email
     */
    @NotBlank
    @Email(regexp = EMAIL_REGEX)
    @Size(min = VALUE_1, max = EMAIL_MAX_SIZE)
    @Schema(description = "Receiver email", example = "bat1238@yandex.ru", required = true)
    private String receiver;

    /**
     * Template code
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Email template code", example = "NEW_EXPERIMENT", required = true)
    private String templateCode;

    /**
     * Email message variables
     */
    @ArraySchema(schema = @Schema(description = "Email templates variables"), maxItems = MAX_VARIABLES_SIZE)
    @Size(max = MAX_VARIABLES_SIZE)
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
