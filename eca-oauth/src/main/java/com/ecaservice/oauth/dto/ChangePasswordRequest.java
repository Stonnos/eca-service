package com.ecaservice.oauth.dto;

import com.ecaservice.oauth.validation.annotations.UserPassword;
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
    @UserPassword
    @ApiModelProperty(value = "Old password", required = true)
    private String oldPassword;

    /**
     * New password
     */
    @NotBlank
    @ApiModelProperty(value = "New password", required = true)
    private String newPassword;
}
