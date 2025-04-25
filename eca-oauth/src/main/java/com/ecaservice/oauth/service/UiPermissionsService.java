package com.ecaservice.oauth.service;

import com.ecaservice.common.web.resource.JsonResourceLoader;
import com.ecaservice.oauth.mapping.MenuItemMapper;
import com.ecaservice.oauth.model.UIPermissionsModel;
import com.ecaservice.web.dto.model.UiPermissionsDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * UI permissions service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UiPermissionsService {

    private static final String UI_PERMISSIONS_CONFIG_JSON = "ui-permissions.json";

    private final SecurityContextProvider securityContextProvider;
    private final MenuItemMapper menuItemMapper;

    private final JsonResourceLoader jsonResourceLoader = new JsonResourceLoader();

    private UIPermissionsModel uiPermissionsModel;

    /**
     * Loads ui permissions config from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void loadConfig() throws IOException {
        uiPermissionsModel = jsonResourceLoader.load(UI_PERMISSIONS_CONFIG_JSON, UIPermissionsModel.class);
    }

    /**
     * Gets current user ui permissions.
     *
     * @return current user ui permissions
     */
    public UiPermissionsDto geUIPermissions() {
        var authentication = securityContextProvider.getAuthentication();
        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("Gets ui permissions for user [{}] with roles [{}]", authentication.getName(), authorities);
        var allowedMenuItem = uiPermissionsModel.getMenuItems()
                .stream()
                .filter(menuItem -> CollectionUtils.isEmpty(menuItem.getAvailableRoles()) ||
                        CollectionUtils.containsAny(authorities, menuItem.getAvailableRoles()))
                .collect(Collectors.toList());
        var menuItemsList = menuItemMapper.map(allowedMenuItem);
        UiPermissionsDto uiPermissionsDto = new UiPermissionsDto();
        uiPermissionsDto.setMenuItems(menuItemsList);
        log.info("Got ui permissions {} for user [{}]", uiPermissionsDto, authentication.getName());
        return uiPermissionsDto;
    }
}
