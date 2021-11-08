package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Menu item model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Menu item model")
public class MenuItemDto {

    /**
     * Item label
     */
    @Schema(description = "Item label", example = "Эксперименты")
    private String label;

    /**
     * Router link
     */
    @Schema(description = "Router link", example = "/dashboard/experiments")
    private String routerLink;

    /**
     * Menu items
     */
    @Schema(description = "Menu items")
    private List<MenuItemDto> items;
}
