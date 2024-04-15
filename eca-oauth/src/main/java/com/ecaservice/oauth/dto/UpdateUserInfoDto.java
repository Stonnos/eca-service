package com.ecaservice.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
    @Schema(description = "First name", example = "Roman", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    /**
     * Last name
     */
    @NotBlank
    @Size(min = PERSON_NAME_MIN_SIZE, max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @Schema(description = "Last name", example = "Batygin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    /**
     * Middle name
     */
    @NotBlank
    @Size(min = PERSON_NAME_MIN_SIZE, max = PERSON_NAME_MAX_SIZE)
    @Pattern(regexp = PERSON_NAME_REGEX)
    @Schema(description = "Middle name", example = "Igorevich", requiredMode = Schema.RequiredMode.REQUIRED)
    private String middleName;
}
