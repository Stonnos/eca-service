package com.ecaservice.web.service;

import com.ecaservice.web.config.MenuBarConfigProvider;
import com.ecaservice.web.dto.model.MenuItemDto;
import com.ecaservice.web.mapping.MenuItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Menu bar service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuBarService {

    private final MenuBarConfigProvider menuBarConfigProvider;
    private final AuthenticationService authenticationService;
    private final MenuItemMapper menuItemMapper;

    /**
     * Gets menu items list.
     *
     * @return menu items list
     */
    public List<MenuItemDto> getMenuItems() {
        var authentication = authenticationService.getAuthentication();
        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("Gets menu bar fow user [{}]", authentication.getName());
        var menuItems = menuBarConfigProvider.getMenuItems()
                .stream()
                .filter(menuItem -> CollectionUtils.isEmpty(menuItem.getAvailableRoles()) ||
                        CollectionUtils.containsAny(authorities, menuItem.getAvailableRoles()))
                .collect(Collectors.toList());
        var menuItemsList = menuItemMapper.map(menuItems);
        log.info("Got menu bar {} for user [{}]", menuItemsList, authentication.getName());
        return menuItemsList;
    }
}
