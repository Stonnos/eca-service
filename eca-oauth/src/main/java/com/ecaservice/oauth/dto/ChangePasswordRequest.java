package com.ecaservice.oauth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Change password request model")
public class ChangePasswordRequest {

    /**
     * Old password
     */
    @NotBlank
    @ApiModelProperty(value = "Old password", example = "oldPassw0rd!", required = true)
    private String oldPassword;

    /**
     * New password
     */
    @NotBlank
    @ApiModelProperty(value = "New password", example = "newPassw0rd!", required = true)
    private String newPassword;
}
