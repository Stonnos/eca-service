package com.ecaservice.oauth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Reset password request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Reset password request model")
public class ResetPasswordRequest {

    /**
     * Token value
     */
    @NotBlank
    @ApiModelProperty(value = "Token value", required = true)
    private String token;

    /**
     * New password
     */
    @NotBlank
    @ApiModelProperty(value = "New password", required = true)
    private String password;
}
