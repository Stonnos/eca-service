package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Push token model.
 *
 * @author Roman Baatygin
 */
@Data
@Builder
@Schema(description = "Push token model")
public class PushTokenDto {

    /**
     * Token id
     */
    @Schema(description = "Token id",
            maxLength = MAX_LENGTH_255, example = "1d2de514-3a87-4620-9b97-c260e24340de", required = true)
    private String tokenId;
}
