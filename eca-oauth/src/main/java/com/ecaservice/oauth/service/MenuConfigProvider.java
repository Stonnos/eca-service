package com.ecaservice.oauth.service;

import com.ecaservice.oauth.model.MenuItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Menu config provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class MenuConfigProvider {

    private static final String MENU_CONFIG_JSON = "menu-config.json";

    @Getter
    private List<MenuItem> menuItems;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Loads menu bar config from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void loadConfig() throws IOException {
        log.info("Starting to load user menu config from resource [{}]", MENU_CONFIG_JSON);
        var resource = resolver.getResource(MENU_CONFIG_JSON);
        @Cleanup var inputStream = resource.getInputStream();
        menuItems = objectMapper.readValue(inputStream, new TypeReference<>() {
        });
        log.info("User menu config has been loaded from resource [{}]", MENU_CONFIG_JSON);
    }
}
