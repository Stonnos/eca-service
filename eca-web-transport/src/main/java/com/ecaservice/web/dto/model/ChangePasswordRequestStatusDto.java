package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Change password request status dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Change password request status")
public class ChangePasswordRequestStatusDto {

    /**
     * Is request active?
     */
    @Schema(description = "Is request active (created and not expired and not confirmed)?")
    private boolean active;
}
