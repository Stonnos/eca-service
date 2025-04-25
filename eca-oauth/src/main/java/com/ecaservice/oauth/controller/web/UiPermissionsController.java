package com.ecaservice.oauth.controller.web;

import com.ecaservice.oauth.service.UiPermissionsService;
import com.ecaservice.web.dto.model.UiPermissionsDto;
import io.swagger.v3.oas.annotations.Operation;
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

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

/**
 * UI permissions API controller.
 *
 * @author Roman Batygin
 */
@Tag(name = "UI permissions API")
@Slf4j
@RestController
@RequestMapping("/ui-permissions")
@RequiredArgsConstructor
public class UiPermissionsController {

    private final UiPermissionsService uiPermissionsService;

    /**
     * Gets current user ui permissions for web application.
     *
     * @return menu items list
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets current user ui permissions for web application",
            summary = "Gets current user ui permissions for web application",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UiPermissionsResponse",
                                                    ref = "#/components/examples/UiPermissionsResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = UiPermissionsDto.class)
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
    @GetMapping
    public UiPermissionsDto getUiPermissions() {
        return uiPermissionsService.geUIPermissions();
    }
}
