package com.ecaservice.web.service;

import com.ecaservice.web.model.MenuItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * Menu bar config provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class MenuBarConfigProvider {

    private static final String MENU_BAR_JSON = "menu-bar.json";

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
        log.info("Starting to load config from resource [{}]", MENU_BAR_JSON);
        var resource = resolver.getResource(MENU_BAR_JSON);
        @Cleanup var inputStream = resource.getInputStream();
        menuItems = objectMapper.readValue(inputStream, new TypeReference<>() {
        });
        log.info("Config has been loaded from resource [{}]", MENU_BAR_JSON);
    }
}
