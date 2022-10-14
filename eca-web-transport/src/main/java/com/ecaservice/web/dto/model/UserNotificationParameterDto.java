package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * User notification parameter dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User notification parameter")
public class UserNotificationParameterDto {

    /**
     * Parameter name
     */
    @Schema(description = "Parameter name", example = "classifiersConfigurationId", maxLength = MAX_LENGTH_255)
    private String name;

    /**
     * Parameter value
     */
    @Schema(description = "Parameter value", example = "250", maxLength = MAX_LENGTH_255)
    private String value;
}
