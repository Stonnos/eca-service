package com.ecaservice.oauth.dto;

import com.ecaservice.oauth.validation.annotations.UserEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Forgot password request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Forgot password request model")
public class ForgotPasswordRequest {

    /**
     * User email
     */
    @NotBlank
    @UserEmail
    @Schema(description = "User email", example = "bat1238@yandex.ru", required = true)
    private String email;
}
