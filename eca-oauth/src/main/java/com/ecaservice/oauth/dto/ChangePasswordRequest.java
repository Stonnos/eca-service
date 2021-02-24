package com.ecaservice.oauth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
     * User id
     */
    @NotNull
    @ApiModelProperty(value = "User id", required = true)
    private Long userId;

    /**
     * Old password
     */
    @NotBlank
    @ApiModelProperty(value = "Old password", required = true)
    private String oldPassword;

    /**
     * New password
     */
    @NotBlank
    @ApiModelProperty(value = "New password", required = true)
    private String newPassword;
}
