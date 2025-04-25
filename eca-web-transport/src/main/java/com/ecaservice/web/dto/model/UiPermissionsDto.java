package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * UI permissions dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "UI permissions model")
public class UiPermissionsDto {

    /**
     * Available menu items
     */
    @Schema(description = "Available menu items")
    private List<MenuItemDto> menuItems;
}
