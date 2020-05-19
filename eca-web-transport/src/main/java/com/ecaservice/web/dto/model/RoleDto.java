package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Role dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "User role model")
public class RoleDto {

    /**
     * Role name
     */
    @ApiModelProperty(value = "Role name")
    private String roleName;

    /**
     * Role description
     */
    @ApiModelProperty(value = "Role description")
    private String description;
}