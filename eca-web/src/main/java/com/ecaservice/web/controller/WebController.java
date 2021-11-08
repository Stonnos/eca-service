package com.ecaservice.web.controller;

import com.ecaservice.web.dto.model.MenuItemDto;
import com.ecaservice.web.service.MenuBarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "API for web application")
@Slf4j
@RestController
@RequestMapping("/api/web")
@RequiredArgsConstructor
public class WebController {

    private final MenuBarService menuBarService;

    /**
     * Gets menu bar for web application.
     *
     * @return menu items list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets menu bar for web application",
            summary = "Gets menu bar for web application"
    )
    @GetMapping(value = "/menu-bar")
    public List<MenuItemDto> getMenuItems() {
        return menuBarService.getMenuItems();
    }
}
