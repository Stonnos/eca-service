package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Change email request status dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Change email request status")
public class ChangeEmailRequestStatusDto {

    @Tolerate
    public ChangeEmailRequestStatusDto() {
    }

    /**
     * Change email request token value
     */
    @Schema(description = "Change email request token value", maxLength = MAX_LENGTH_255,
            example = "1d2de514-3a87-4620-9b97-c260e24340de")
    private String token;

    /**
     * New email.
     */
    @Schema(description = "New email", maxLength = MAX_LENGTH_255)
    private String newEmail;

    /**
     * Is request active?
     */
    @Schema(description = "Is request active (created and not expired and not confirmed)?")
    private boolean active;
}
