package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

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
    @Schema(description = "Item label", example = "Эксперименты", maxLength = MAX_LENGTH_255)
    private String label;

    /**
     * Router link
     */
    @Schema(description = "Router link", example = "/dashboard/experiments", maxLength = MAX_LENGTH_255)
    private String routerLink;

    /**
     * Menu items
     */
    @Schema(description = "Menu items")
    private List<MenuItemDto> items;
}
