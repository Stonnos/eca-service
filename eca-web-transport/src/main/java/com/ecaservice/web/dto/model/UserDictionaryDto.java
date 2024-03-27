package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * User dictionary dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User dictionary model")
public class UserDictionaryDto {

    /**
     * User login
     */
    @Schema(description = "User login", example = "admin", maxLength = MAX_LENGTH_255)
    private String login;

    /**
     * User full name
     */
    @Schema(description = "User full name", example = "Ivanov Ivan Ivanovich", maxLength = MAX_LENGTH_255)
    private String fullName;
}
