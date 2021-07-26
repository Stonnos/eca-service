package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Role dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "User role model")
public class RoleDto {

    /**
     * Role name
     */
    @Schema(description = "Role name")
    private String roleName;

    /**
     * Role description
     */
    @Schema(description = "Role description")
    private String description;
}
