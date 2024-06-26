package com.ecaservice.notification.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Schema(description = "Request id", example = "1d2de514-3a87-4620-9b97-c260e24340de",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String requestId;

    /**
     * Correlation id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = VALUE_1, max = UUID_MAX_SIZE)
    @Schema(description = "Correlation id",
            example = "98a57ab7-6494-4d9d-b793-c807fdf02692", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correlationId;

    /**
     * Receiver email
     */
    @NotBlank
    @Email(regexp = EMAIL_REGEX)
    @Size(min = VALUE_1, max = EMAIL_MAX_SIZE)
    @Schema(description = "Receiver email", example = "bat1238@yandex.ru", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiver;

    /**
     * Template code
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Email template code", example = "NEW_EXPERIMENT",
            requiredMode = Schema.RequiredMode.REQUIRED)
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
