package com.ecaservice.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_MIN_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_REGEX;

/**
 * Update user info model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Update user info model")
public class UpdateUserInfoDto {

    /**
     * First name
     */
    @NotBlank
    @Size(min = PERSON_NAME_MIN_SIZE, max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @Schema(description = "First name", example = "Roman", required = true)
    private String firstName;

    /**
     * Last name
     */
    @NotBlank
    @Size(min = PERSON_NAME_MIN_SIZE, max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @Schema(description = "Last name", example = "Batygin", required = true)
    private String lastName;

    /**
     * Middle name
     */
    @NotBlank
    @Size(min = PERSON_NAME_MIN_SIZE, max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @Schema(description = "Middle name", example = "Igorevich", required = true)
    private String middleName;
}
