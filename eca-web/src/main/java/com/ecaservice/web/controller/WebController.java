package com.ecaservice.web.controller;

import com.ecaservice.web.dto.model.MenuItemDto;
import com.ecaservice.web.service.MenuBarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

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
            summary = "Gets menu bar for web application",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "MenuItemsResponse",
                                                    ref = "#/components/examples/MenuItemsResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = MenuItemDto.class))
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/menu-bar")
    public List<MenuItemDto> getMenuItems() {
        return menuBarService.getMenuItems();
    }
}
