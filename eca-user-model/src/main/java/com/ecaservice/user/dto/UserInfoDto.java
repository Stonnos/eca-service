package com.ecaservice.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.user.util.FieldConstraints.MAX_LENGTH_255;

/**
 * User info dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User model")
public class UserInfoDto {

    /**
     * User login
     */
    @Schema(description = "User login", example = "admin", maxLength = MAX_LENGTH_255)
    private String login;

    /**
     * User email
     */
    @Schema(description = "User email", example = "test@mail.ru", maxLength = MAX_LENGTH_255)
    private String email;

    /**
     * User first name
     */
    @Schema(description = "User first name", example = "Ivan", maxLength = MAX_LENGTH_255)
    private String firstName;

    /**
     * User last name
     */
    @Schema(description = "User last name", example = "Ivanov", maxLength = MAX_LENGTH_255)
    private String lastName;

    /**
     * User middle name
     */
    @Schema(description = "User middle name", example = "Ivanovich", maxLength = MAX_LENGTH_255)
    private String middleName;

    /**
     * User full name
     */
    @Schema(description = "User full name", example = "Ivanov Ivan Ivanovich", maxLength = MAX_LENGTH_255)
    private String fullName;

    /**
     * Account locked?
     */
    @Schema(description = "Account locked")
    private boolean locked;
}
