package com.ecaservice.web.push.controller.web;

import com.ecaservice.web.dto.model.PushTokenDto;
import com.ecaservice.web.push.service.UserService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

/**
 * Implements REST API to manage with push tokens for web applications.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API to manage with push tokens for web applications")
@RestController
@RequiredArgsConstructor
public class PushTokenController {

    private final UserService userService;

    /**
     * Obtains push token for current user.
     *
     * @return push token dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Obtains push token for current user",
            summary = "Obtains push token for current user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "PushTokenResponse",
                                                    ref = "#/components/examples/PushTokenResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = PushTokenDto.class)
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
    @PostMapping(value = "/push/token")
    public PushTokenDto obtainToken() {
        var user = userService.getCurrentUser();
        log.info("Received request to get push token for user [{}]", user);
        return null;
    }
}
