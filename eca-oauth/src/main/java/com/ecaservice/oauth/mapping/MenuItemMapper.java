package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.model.MenuItem;
import com.ecaservice.web.dto.model.MenuItemDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Menu item mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface MenuItemMapper {

    /**
     * Maps menu item to dto model.
     *
     * @param menuItem - menu item
     * @return menu item dto
     */
    MenuItemDto map(MenuItem menuItem);

    /**
     * Maps menu items to dto models.
     *
     * @param menuItems - menu items
     * @return menu items dto models
     */
    List<MenuItemDto> map(List<MenuItem> menuItems);
}
