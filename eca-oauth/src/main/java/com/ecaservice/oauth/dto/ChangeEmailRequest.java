package com.ecaservice.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_REGEX;
import static com.ecaservice.oauth.util.FieldConstraints.PASSWORD_REGEX;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Change email request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Change email request model")
public class ChangeEmailRequest {

    /**
     * New user email
     */
    @Email(regexp = EMAIL_REGEX)
    @Size(min = VALUE_1, max = EMAIL_MAX_SIZE)
    @Schema(description = "New user email", example = "bat1238@yandex.ru", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newEmail;

    /**
     * User account password to confirm action
     */
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX)
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "User account password to conform action", example = "passw0rd!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
