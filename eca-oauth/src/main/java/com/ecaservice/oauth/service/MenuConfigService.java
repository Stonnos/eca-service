package com.ecaservice.oauth.service;

import com.ecaservice.oauth.mapping.MenuItemMapper;
import com.ecaservice.web.dto.model.MenuItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Menu config service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuConfigService {

    private final MenuConfigProvider menuConfigProvider;
    private final SecurityContextProvider securityContextProvider;
    private final MenuItemMapper menuItemMapper;

    /**
     * Gets current user menu config.
     *
     * @return current user menu config
     */
    public List<MenuItemDto> getMenuConfig() {
        var authentication = securityContextProvider.getAuthentication();
        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("Gets menu config fow user [{}]", authentication.getName());
        var menuItems = menuConfigProvider.getMenuItems()
                .stream()
                .filter(menuItem -> CollectionUtils.isEmpty(menuItem.getAvailableRoles()) ||
                        CollectionUtils.containsAny(authorities, menuItem.getAvailableRoles()))
                .collect(Collectors.toList());
        var menuItemsList = menuItemMapper.map(menuItems);
        log.info("Got menu config {} for user [{}]", menuItemsList, authentication.getName());
        return menuItemsList;
    }
}
