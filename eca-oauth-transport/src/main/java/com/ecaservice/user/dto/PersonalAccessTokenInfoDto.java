package com.ecaservice.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

import static com.ecaservice.user.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Personal access token info dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User model")
public class PersonalAccessTokenInfoDto {

    /**
     * User login
     */
    @Schema(description = "User login", example = "admin", maxLength = MAX_LENGTH_255)
    private String user;

    /**
     * User login
     */
    @Schema(description = "Token type", example = "USER_TOKEN", maxLength = MAX_LENGTH_255)
    private PersonalAccessTokenType tokenType;

    /**
     * Token valid?
     */
    @Schema(description = "Token valid?")
    private boolean valid;
}
