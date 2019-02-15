package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@ApiModel(description = "User model")
public class UserDto {

    /**
     * User login
     */
    @ApiModelProperty(value = "User login")
    private String login;
}
