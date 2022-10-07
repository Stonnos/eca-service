package com.ecaservice.web.push.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

import static com.ecaservice.web.push.dto.FieldConstraints.MAX_ADDITIONAL_PROPERTIES_SIZE;
import static com.ecaservice.web.push.dto.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.push.dto.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.push.dto.FieldConstraints.UUID_PATTERN;
import static com.ecaservice.web.push.dto.FieldConstraints.VALUE_1;

/**
 * Abstract push request model.
 *
 * @author Roman Batygin
 */
@Data
public abstract class AbstractPushRequest {

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
     * Additional properties
     */
    @ArraySchema(schema = @Schema(description = "Additional properties"), maxItems = MAX_ADDITIONAL_PROPERTIES_SIZE)
    @Size(max = MAX_ADDITIONAL_PROPERTIES_SIZE)
    private Map<@NotBlank String, @NotBlank String> additionalProperties;
}
