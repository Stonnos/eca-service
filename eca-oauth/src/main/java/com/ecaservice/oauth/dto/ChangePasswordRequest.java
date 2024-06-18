package com.ecaservice.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.ecaservice.oauth.util.FieldConstraints.PASSWORD_REGEX;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Change password request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Change password request model")
public class ChangePasswordRequest {

    /**
     * Old password
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Old password", example = "oldPassw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    /**
     * New password
     */
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX)
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "New password", example = "newPassw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
