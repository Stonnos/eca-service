package com.ecaservice.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ecaservice.oauth.util.FieldConstraints.MIN_PASSWORD_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.PASSWORD_REGEX;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Reset password request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reset password request model")
public class ResetPasswordRequest {

    /**
     * Token value
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Token value",
            example = "MDhmNTg4MDdiMTI0Y2Y4OWNmN2UxYmE1OTljYjUzOWU6MTYxNjE1MzM4MDMzMQ==", required = true)
    private String token;

    /**
     * New password
     */
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX)
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_LENGTH_255)
    @Schema(description = "New password", example = "passw0rd!", required = true)
    private String password;
}
