package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

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

    /**
     * New email.
     */
    @Schema(description = "New email", maxLength = MAX_LENGTH_255)
    private String newEmail;

    /**
     * Is request valid?
     */
    @Schema(description = "Is request valid (created and not expired and not confirmed)?")
    private boolean valid;
}
