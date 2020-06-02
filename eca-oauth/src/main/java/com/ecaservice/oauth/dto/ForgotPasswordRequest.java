package com.ecaservice.oauth.dto;

import com.ecaservice.oauth.validation.annotations.UserEmail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Forgot password request model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Forgot password request model")
public class ForgotPasswordRequest {

    /**
     * User email
     */
    @NotBlank
    @UserEmail
    @ApiModelProperty(value = "User email", required = true)
    private String email;
}
