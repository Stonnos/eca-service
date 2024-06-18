package com.ecaservice.oauth.controller.internal;

import com.ecaservice.oauth.service.UserService;
import com.ecaservice.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Size;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_INTERNAL_API;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Implements internal API for users.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Tag(name = "Internal users API")
@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    /**
     * Gets user info.
     *
     * @param login - user login
     * @return user info dto
     */
    @PreAuthorize("hasAuthority('SCOPE_internal-api')")
    @Operation(
            description = "Gets user info",
            summary = "Gets user info",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_INTERNAL_API),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InternalUserInfoResponse",
                                                    ref = "#/components/examples/InternalUserInfoResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = UserInfoDto.class)
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
    @GetMapping(value = "/user-info")
    public UserInfoDto getUserInfo(@Parameter(description = "User login", required = true)
                                   @Size(min = VALUE_1, max = MAX_LENGTH_255)
                                   @RequestParam String login) {
        log.debug("Request get user info by login [{}]", login);
        return userService.getUserInfo(login);
    }
}
