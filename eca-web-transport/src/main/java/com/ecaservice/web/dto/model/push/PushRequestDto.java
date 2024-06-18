package com.ecaservice.web.dto.model.push;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_ADDITIONAL_PROPERTIES_SIZE;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Push request web socket transport model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Push request web socket transport model")
public class PushRequestDto {

    /**
     * Push type
     */
    @NotNull
    @Schema(description = "Push type", minLength = VALUE_1, maxLength = MAX_LENGTH_255, requiredMode = Schema.RequiredMode.REQUIRED)
    private PushType pushType;

    /**
     * Request id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = VALUE_1, max = UUID_MAX_LENGTH)
    @Schema(description = "Request id",
            example = "1d2de514-3a87-4620-9b97-c260e24340de", requiredMode = Schema.RequiredMode.REQUIRED)
    private String requestId;

    /**
     * Correlation id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = VALUE_1, max = UUID_MAX_LENGTH)
    @Schema(description = "Correlation id",
            example = "98a57ab7-6494-4d9d-b793-c807fdf02692", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correlationId;

    /**
     * Message type
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Message type", example = "EXPERIMENT_STATUS", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = "EXPERIMENT_STATUS")
    private String messageType;

    /**
     * Message text
     */
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Message text", example = "Message text")
    private String messageText;

    /**
     * Push initiator, for example user login
     */
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Push initiator, for example user login")
    private String initiator;

    /**
     * Additional properties
     */
    @ArraySchema(schema = @Schema(description = "Additional properties"), maxItems = MAX_ADDITIONAL_PROPERTIES_SIZE)
    @Size(max = MAX_ADDITIONAL_PROPERTIES_SIZE)
    private Map<@NotBlank String, @NotBlank String> additionalProperties;

    /**
     * Show push message?
     */
    @Schema(description = "Show push message?")
    private boolean showMessage;
}
