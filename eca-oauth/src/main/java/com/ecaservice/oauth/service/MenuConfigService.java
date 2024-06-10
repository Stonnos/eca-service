package com.ecaservice.oauth.service;

import com.ecaservice.common.web.resource.JsonResourceLoader;
import com.ecaservice.oauth.mapping.MenuItemMapper;
import com.ecaservice.oauth.model.MenuItem;
import com.ecaservice.web.dto.model.MenuItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
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

    private static final String MENU_CONFIG_JSON = "menu-config.json";

    private final SecurityContextProvider securityContextProvider;
    private final MenuItemMapper menuItemMapper;

    private final JsonResourceLoader jsonResourceLoader = new JsonResourceLoader();

    private List<MenuItem> menuItems;

    /**
     * Loads menu bar config from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void loadConfig() throws IOException {
        menuItems = jsonResourceLoader.load(MENU_CONFIG_JSON, new TypeReference<>() {
        });
    }

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
        var allowedMenuItem = menuItems
                .stream()
                .filter(menuItem -> CollectionUtils.isEmpty(menuItem.getAvailableRoles()) ||
                        CollectionUtils.containsAny(authorities, menuItem.getAvailableRoles()))
                .collect(Collectors.toList());
        var menuItemsList = menuItemMapper.map(allowedMenuItem);
        log.info("Got menu config {} for user [{}]", menuItemsList, authentication.getName());
        return menuItemsList;
    }
}
