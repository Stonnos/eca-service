package com.ecaservice.web.push.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@Setter
@Getter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "pushType",
        include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SystemPushRequest.class, name = "SYSTEM"),
        @JsonSubTypes.Type(value = UserPushNotificationRequest.class, name = "USER_NOTIFICATION")
})
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractPushRequest {

    /**
     * Push type
     */
    @NotNull
    @Schema(description = "Push type", minLength = VALUE_1, maxLength = MAX_LENGTH_255)
    private final PushType pushType;

    /**
     * Request id (used for cross system logging)
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = VALUE_1, max = UUID_MAX_LENGTH)
    @Schema(description = "Request id (used for cross system logging)",
            example = "1d2de514-3a87-4620-9b97-c260e24340de", requiredMode = Schema.RequiredMode.REQUIRED)
    private String requestId;

    /**
     * Message type
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Message type", example = "EXPERIMENT_STATUS", requiredMode = Schema.RequiredMode.REQUIRED)
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
