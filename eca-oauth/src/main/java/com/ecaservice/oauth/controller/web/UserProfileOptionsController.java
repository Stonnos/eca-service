package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.oauth.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.oauth.service.UserProfileOptionsService;
import com.ecaservice.web.dto.model.UserProfileNotificationOptionsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

/**
 * Implements user profile options REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Tag(name = "User profile options API for web application")
@RestController
@RequestMapping("/user/profile/options")
@RequiredArgsConstructor
public class UserProfileOptionsController {

    private final UserProfileOptionsService userProfileOptionsService;

    /**
     * Gets user profile notification options.
     *
     * @param principal - principal object
     * @return user profile notification options
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets user profile notification options",
            summary = "Gets user profile notification options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UserProfileNotificationOptionsResponse",
                                                    ref = "#/components/examples/UserProfileNotificationOptionsResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = UserProfileNotificationOptionsDto.class)
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
    @GetMapping(value = "/notifications")
    public UserProfileNotificationOptionsDto getUserNotificationOptions(Principal principal) {
        log.info("Get user [{}] notification options", principal.getName());
        return userProfileOptionsService.getUserNotificationOptions(principal.getName());
    }

    /**
     * Updates user profile notification options.
     *
     * @param principal                        - principal object
     * @param updateUserNotificationOptionsDto - notification options dto for update
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Updates user profile notification options",
            summary = "Updates user profile notification options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "UpdateUserProfileNotificationOptionsRequest",
                                    ref = "#/components/examples/UpdateUserProfileNotificationOptionsRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
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
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DuplicateNotificationEventResponse",
                                                    ref = "#/components/examples/DuplicateNotificationEventResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/update-notifications")
    public void updateUserNotificationOptions(Principal principal,
                                              @Valid @RequestBody
                                              UpdateUserNotificationOptionsDto updateUserNotificationOptionsDto) {
        log.info("Received request to update user [{}] notification options", principal.getName());
        userProfileOptionsService.updateUserNotificationOptions(principal.getName(), updateUserNotificationOptionsDto);
        log.info("Request to update user [{}] notification options has been processed", principal.getName());
    }
}
