package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * User dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "User model")
public class UserDto {

    /**
     * User login
     */
    @ApiModelProperty(value = "User login")
    private String login;

    /**
     * User email
     */
    @ApiModelProperty(value = "User email")
    private String email;

    /**
     * User first name
     */
    @ApiModelProperty(value = "User first name")
    private String firstName;

    /**
     * Roles list
     */
    @ApiModelProperty(value = "User roles")
    private List<RoleDto> roles;
}