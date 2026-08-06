package com.ecaservice.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.oauth.util.FieldConstraints.PASSWORD_REGEX;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Force set password request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Force set password request model")
public class ForceSetPasswordRequest {

    /**
     * Token value
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Token value", example = "MDhmNTg4MDdiMTI0Y2Y4OWNmN2UxYmE1OTljYjUzOWU6MTYxNjE1MzM4MDMzMQ==",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    /**
     * Confirmation code
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Confirmation code", example = "633478", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmationCode;

    /**
     * New password
     */
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX)
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "New password", example = "passw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
