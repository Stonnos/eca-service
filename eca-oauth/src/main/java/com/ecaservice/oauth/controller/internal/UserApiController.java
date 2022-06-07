package com.ecaservice.oauth.controller.internal;

import com.ecaservice.oauth.service.UserService;
import com.ecaservice.user.dto.UserInfoDto;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_INTERNAL_API;
import static com.ecaservice.oauth.controller.doc.ApiExamples.UNAUTHORIZED_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.USER_INFO_RESPONSE_JSON;

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
    @PreAuthorize("#oauth2.hasScope('internal-api')")
    @Operation(
            description = "Gets user info",
            summary = "Gets user info",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_INTERNAL_API),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = USER_INFO_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = UserInfoDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/user-info")
    public UserInfoDto getUserInfo(@RequestParam String login) {
       log.debug("Request get user info by login [{}]", login);
       return userService.getUserInfo(login);
    }
}
