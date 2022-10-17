package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Push token model.
 *
 * @author Roman Baatygin
 */
@Data
@Schema(description = "Push tokent model")
public class PushTokenDto {

    /**
     * Token id
     */
    @Schema(description = "Token id",
            maxLength = MAX_LENGTH_255, example = "1d2de514-3a87-4620-9b97-c260e24340de", required = true)
    private String tokenId;

    /**
     * Token expiration in minutes
     */
    @Schema(description = "Token expiration in minutes", example = "240", minimum = VALUE_1_STRING,
            maximum = MAX_INTEGER_VALUE_STRING, required = true)
    private Integer expiresIn;
}
