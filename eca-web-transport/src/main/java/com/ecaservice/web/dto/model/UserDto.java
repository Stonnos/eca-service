package com.ecaservice.web.dto.model;

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
public class UserDto {

    /**
     * User login
     */
    @ApiModelProperty(notes = "User login")
    private String login;
}
