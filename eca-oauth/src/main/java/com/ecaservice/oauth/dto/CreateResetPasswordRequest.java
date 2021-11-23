package com.ecaservice.oauth.dto;

import com.ecaservice.oauth.validation.annotations.UserEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

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
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "User email", example = "bat1238@yandex.ru", required = true)
    private String email;
}
