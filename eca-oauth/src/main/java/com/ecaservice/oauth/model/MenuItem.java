package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

/**
 * Menu item model.
 *
 * @author Roman Batygin
 */
@Data
public class MenuItem {

    /**
     * Item label
     */
    private String label;

    /**
     * Router link
     */
    private String routerLink;

    /**
     * Available roles
     */
    private List<String> availableRoles;

    /**
     * Menu items
     */
    private List<MenuItem> items;
}
