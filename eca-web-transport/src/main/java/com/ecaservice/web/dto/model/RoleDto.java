package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

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
    @Schema(description = "Role name", example = "ROLE_SUPER_ADMIN", maxLength = MAX_LENGTH_255)
    private String roleName;

    /**
     * Role description
     */
    @Schema(description = "Role description", example = "Administrator", maxLength = MAX_LENGTH_255)
    private String description;
}
