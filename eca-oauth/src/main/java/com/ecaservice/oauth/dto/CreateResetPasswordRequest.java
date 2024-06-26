package com.ecaservice.oauth.dto;

import com.ecaservice.oauth.validation.annotations.UserEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Create reset password request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create reset password request model")
public class CreateResetPasswordRequest {

    /**
     * User email
     */
    @NotBlank
    @UserEmail
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "User email", example = "bat1238@yandex.ru", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
