package com.ecaservice.oauth.model;

import lombok.Data;

import java.util.List;

/**
 * UI permissions model.
 *
 * @author Roman Batygin
 */
@Data
public class UIPermissionsModel {

    /**
     * Available menu items
     */
    private List<MenuItem> menuItems;
}
