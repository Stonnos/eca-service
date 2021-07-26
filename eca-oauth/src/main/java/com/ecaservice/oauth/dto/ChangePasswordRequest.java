package com.ecaservice.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

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
    @Schema(description = "Old password", example = "oldPassw0rd!", required = true)
    private String oldPassword;

    /**
     * New password
     */
    @NotBlank
    @Schema(description = "New password", example = "newPassw0rd!", required = true)
    private String newPassword;
}
