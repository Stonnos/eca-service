package com.ecaservice.oauth.controller.internal;

import com.ecaservice.oauth.service.UserProfileOptionsService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

/**
 * Implements user profile options internal REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Tag(name = "User profile options internal API")
@RestController
@RequestMapping("/api/internal/user/options")
@RequiredArgsConstructor
public class UserProfileOptionsInternalApiController {

    private final UserProfileOptionsService userProfileOptionsService;

    /**
     * Gets user profile options.
     *
     * @return user profile options
     */
    @Operation(
            description = "Gets user profile options",
            summary = "Gets user profile options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UserProfileOptionsResponse",
                                                    ref = "#/components/examples/UserProfileOptionsResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = UserProfileOptionsDto.class)
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
    @GetMapping(value = "/details")
    public UserProfileOptionsDto getUserProfileOptions(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Get user [{}] profile options", userDetails.getId());
        return userProfileOptionsService.getUserProfileOptions(userDetails.getUsername());
    }
}
