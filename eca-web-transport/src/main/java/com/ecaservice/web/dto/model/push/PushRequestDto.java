package com.ecaservice.web.dto.model.push;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_ADDITIONAL_PROPERTIES_SIZE;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Push request web socket transport model.
 */
@Data
@Schema(description = "Push request web socket transport model")
public class PushRequestDto {

    /**
     * Request id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = VALUE_1, max = UUID_MAX_LENGTH)
    @Schema(description = "Request id", example = "1d2de514-3a87-4620-9b97-c260e24340de", required = true)
    private String requestId;

    /**
     * Message type
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Message type", example = "EXPERIMENT_STATUS", required = true,
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
