package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.model.MenuItem;
import com.ecaservice.web.dto.model.MenuItemDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MenuItemMapper} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(MenuItemMapperImpl.class)
class MenuItemMapperTest {

    private static final String PARENT_LABEL = "Experiments";
    private static final String PARENT_ROUTER_LINK = "/dashboard/experiments";
    private static final String PARENT_ICON = "pi pi-list";
    private static final String PARENT_STYLE_CLASS = "menu-item";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private static final String CHILD_LABEL = "Create experiment";
    private static final String CHILD_ROUTER_LINK = "/dashboard/experiments/create";
    private static final String CHILD_ICON = "pi pi-plus";
    private static final String CHILD_STYLE_CLASS = "sub-menu-item";

    @Autowired
    private MenuItemMapper mapper;

    @Test
    void testMapSingleItemWhenNull() {
        MenuItemDto result = mapper.map((MenuItem) null);
        assertThat(result).isNull();
    }

    @Test
    void testMapSingleItemWithNestedItems() {
        MenuItem child = createMenuItem(CHILD_LABEL, CHILD_ROUTER_LINK, CHILD_ICON, CHILD_STYLE_CLASS, null);
        MenuItem parent = createMenuItem(
                PARENT_LABEL,
                PARENT_ROUTER_LINK,
                PARENT_ICON,
                PARENT_STYLE_CLASS,
                List.of(child)
        );

        MenuItemDto result = mapper.map(parent);
        assertThat(result).isNotNull();
        assertThat(result.getLabel()).isEqualTo(PARENT_LABEL);
        assertThat(result.getRouterLink()).isEqualTo(PARENT_ROUTER_LINK);
        assertThat(result.getIcon()).isEqualTo(PARENT_ICON);
        assertThat(result.getStyleClass()).isEqualTo(PARENT_STYLE_CLASS);
        assertThat(result.getItems()).hasSize(1);
        MenuItemDto childDto = result.getItems().getFirst();
        assertThat(childDto.getLabel()).isEqualTo(CHILD_LABEL);
        assertThat(childDto.getRouterLink()).isEqualTo(CHILD_ROUTER_LINK);
        assertThat(childDto.getIcon()).isEqualTo(CHILD_ICON);
        assertThat(childDto.getStyleClass()).isEqualTo(CHILD_STYLE_CLASS);
        assertThat(childDto.getItems()).isNull();
    }

    @Test
    void testMapListWhenNull() {
        List<MenuItemDto> result = mapper.map((List<MenuItem>) null);
        assertThat(result).isNull();
    }

    @Test
    void testMapListWhenEmpty() {
        List<MenuItemDto> result = mapper.map(Collections.emptyList());
        assertThat(result).isEmpty();
    }

    @Test
    void testMapListSuccess() {
        MenuItem item = createMenuItem(PARENT_LABEL, PARENT_ROUTER_LINK, PARENT_ICON, PARENT_STYLE_CLASS, null);
        List<MenuItemDto> result = mapper.map(List.of(item));
        assertThat(result).hasSize(1);
        MenuItemDto dto = result.getFirst();
        assertThat(dto.getLabel()).isEqualTo(PARENT_LABEL);
        assertThat(dto.getRouterLink()).isEqualTo(PARENT_ROUTER_LINK);
        assertThat(dto.getIcon()).isEqualTo(PARENT_ICON);
        assertThat(dto.getStyleClass()).isEqualTo(PARENT_STYLE_CLASS);
        assertThat(dto.getItems()).isNull();
    }

    private MenuItem createMenuItem(String label, String routerLink, String icon, String styleClass,
                                    List<MenuItem> items) {
        MenuItem menuItem = new MenuItem();
        menuItem.setLabel(label);
        menuItem.setRouterLink(routerLink);
        menuItem.setIcon(icon);
        menuItem.setStyleClass(styleClass);
        menuItem.setAvailableRoles(List.of(ROLE_ADMIN));
        menuItem.setItems(items);
        return menuItem;
    }
}